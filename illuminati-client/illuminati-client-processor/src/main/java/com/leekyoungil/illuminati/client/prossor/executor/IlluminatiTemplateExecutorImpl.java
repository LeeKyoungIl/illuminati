package com.leekyoungil.illuminati.client.prossor.executor;

import com.leekyoungil.illuminati.client.prossor.infra.IlluminatiInfraTemplate;
import com.leekyoungil.illuminati.client.prossor.infra.kafka.impl.KafkaInfraTemplateImpl;
import com.leekyoungil.illuminati.client.prossor.infra.rabbitmq.impl.RabbitmqInfraTemplateImpl;
import com.leekyoungil.illuminati.client.prossor.properties.IlluminatiPropertiesImpl;
import com.leekyoungil.illuminati.common.constant.IlluminatiConstant;
import com.leekyoungil.illuminati.common.dto.IlluminatiTemplateInterfaceModel;
import com.leekyoungil.illuminati.common.properties.IlluminatiPropertiesHelper;
import com.leekyoungil.illuminati.common.util.SystemUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * Created by leekyoungil (leekyoungil@gmail.com) on 12/01/2017.
 */
public class IlluminatiTemplateExecutorImpl implements IlluminatiExecutor<IlluminatiTemplateInterfaceModel> {

    private static final Logger ILLUMINATI_TEMPLATE_EXECUTOR_LOGGER = LoggerFactory.getLogger(IlluminatiTemplateExecutorImpl.class);

    // ################################################################################################################
    // ### init illuminati template queue                                                                           ###
    // ################################################################################################################
    private static final int ILLUMINATI_BAK_LOG = 10000;
    private static final long ILLUMINATI_DEQUEUING_TIMEOUT_MS = 1000;
    private static final long ILLUMINATI_ENQUEUING_TIMEOUT_MS = 0;

    private final static BlockingQueue<IlluminatiTemplateInterfaceModel> ILLUMINATI_MODEL_BLOCKING_QUEUE = new LinkedBlockingQueue<IlluminatiTemplateInterfaceModel>(ILLUMINATI_BAK_LOG);

    // ################################################################################################################
    // ### init illuminati broker                                                                                   ###
    // ################################################################################################################
    private static IlluminatiInfraTemplate ILLUMINATI_TEMPLATE;

    @Override public synchronized void init () {
        if (ILLUMINATI_TEMPLATE == null) {
            ILLUMINATI_TEMPLATE = this.initIlluminatiTemplate();
        }

        if (ILLUMINATI_TEMPLATE != null) {
            this.createSystemThread();
        }
    }

    // ################################################################################################################
    // ### public methods                                                                                           ###
    // ################################################################################################################

    @Override public void addToQueue(final IlluminatiTemplateInterfaceModel illuminatiTemplateInterfaceModel) {
        if (IlluminatiConstant.ILLUMINATI_DEBUG == false) {
            try {
                ILLUMINATI_MODEL_BLOCKING_QUEUE.offer(illuminatiTemplateInterfaceModel, ILLUMINATI_ENQUEUING_TIMEOUT_MS, TimeUnit.MILLISECONDS);
            } catch (InterruptedException e) {
                ILLUMINATI_TEMPLATE_EXECUTOR_LOGGER.warn("Failed to enqueuing the Illuminati queue.. ("+e.getMessage()+")");
            }
        } else {
            this.addToQueueByDebug(illuminatiTemplateInterfaceModel);
        }
    }

    @Override public int getQueueSize () {
        if (ILLUMINATI_MODEL_BLOCKING_QUEUE == null) {
            ILLUMINATI_TEMPLATE_EXECUTOR_LOGGER.warn("ILLUMINATI_TEMPLATE_BLOCKING_QUEUE is must not null.");
            return 0;
        }
        return ILLUMINATI_MODEL_BLOCKING_QUEUE.size();
    }

    @Override public IlluminatiTemplateInterfaceModel deQueue() {
        if (IlluminatiConstant.ILLUMINATI_DEBUG == false) {
            try {
                return ILLUMINATI_MODEL_BLOCKING_QUEUE.poll(ILLUMINATI_DEQUEUING_TIMEOUT_MS, TimeUnit.MILLISECONDS);
            } catch (InterruptedException e) {
                ILLUMINATI_TEMPLATE_EXECUTOR_LOGGER.warn("Failed to dequeing the rabbitmq queue.. ("+e.getMessage()+")");
            }

            return null;
        } else {
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                // ignore
            }

            return this.deQueueByDebug();
        }
    }

    @Override public void sendToNextStep(IlluminatiTemplateInterfaceModel illuminatiTemplateInterfaceModel) {
        // something to check validation.. but.. now not exists.
        if (ILLUMINATI_TEMPLATE == null) {
            ILLUMINATI_TEMPLATE_EXECUTOR_LOGGER.warn("ILLUMINATI_TEMPLATE is must not null.");
            return;
        }
        ILLUMINATI_TEMPLATE.sendToIlluminati(illuminatiTemplateInterfaceModel.getJsonString());
    }

    @Override public void createSystemThread () {
        final Runnable runnableFirst = new Runnable() {
            public void run() {
                while (true) {
                    try {
                        final IlluminatiTemplateInterfaceModel illuminatiTemplateInterfaceModel = deQueue();
                        if (illuminatiTemplateInterfaceModel != null) {
                            if (IlluminatiConstant.ILLUMINATI_DEBUG == false) {
                                sendToNextStep(illuminatiTemplateInterfaceModel);
                            } else {
                                try {
                                    Thread.sleep(5000);
                                } catch (InterruptedException e) {
                                    // ignore
                                }
                                sendToNextStepByDebug(illuminatiTemplateInterfaceModel);
                            }
                        }
                    } catch (Exception e) {
                        ILLUMINATI_TEMPLATE_EXECUTOR_LOGGER.warn("Failed to send the rabbitmq queue.. ("+e.getMessage()+")");
                    }
                }
            }
        };

        SystemUtil.createSystemThread(runnableFirst, "ILLUMINATI_SENDER_THREAD");

        this.createDebugThread();
    }

    public static boolean illuminatiTemplateIsActive () {
        if (ILLUMINATI_TEMPLATE != null && ILLUMINATI_TEMPLATE.canIConnect() == true) {
            return true;
        }

        return false;
    }

    // ################################################################################################################
    // ### private methods                                                                                          ###
    // ################################################################################################################

    private IlluminatiInfraTemplate initIlluminatiTemplate () {
        final String illuminatiBroker = IlluminatiPropertiesHelper.getPropertiesValueByKey(IlluminatiPropertiesImpl.class, null, "illuminati", "broker");
        IlluminatiInfraTemplate illuminatiInfraTemplate;

        if ("kafka".equals(illuminatiBroker)) {
            illuminatiInfraTemplate = new KafkaInfraTemplateImpl("illuminati");
        } else if ("rabbitmq".equals(illuminatiBroker)) {
            illuminatiInfraTemplate = new RabbitmqInfraTemplateImpl("illuminati");
        } else {
            ILLUMINATI_TEMPLATE_EXECUTOR_LOGGER.warn("Sorry. check your properties of Illuminati");
            new Exception("Sorry. check your properties of Illuminati");
            return null;
        }

        if (illuminatiInfraTemplate == null || illuminatiInfraTemplate.canIConnect() == false) {
            return null;
        }

        return illuminatiInfraTemplate;
    }
    /**
     * only execute at debug
     * @param illuminatiTemplateInterfaceModel
     */
    @Override public void addToQueueByDebug (final IlluminatiTemplateInterfaceModel illuminatiTemplateInterfaceModel) {
        // debug illuminati buffer queue
        if (IlluminatiConstant.ILLUMINATI_DEBUG == true) {
            try {
                ILLUMINATI_TEMPLATE_EXECUTOR_LOGGER.info("template queue current size is "+String.valueOf(this.getQueueSize()));
                final long start = System.currentTimeMillis();
                ILLUMINATI_MODEL_BLOCKING_QUEUE.offer(illuminatiTemplateInterfaceModel, ILLUMINATI_ENQUEUING_TIMEOUT_MS, TimeUnit.MILLISECONDS);
                final long elapsedTime = System.currentTimeMillis() - start;
                ILLUMINATI_TEMPLATE_EXECUTOR_LOGGER.info("template queue after inserted size is "+String.valueOf(this.getQueueSize()));
                ILLUMINATI_TEMPLATE_EXECUTOR_LOGGER.info("elapsed time of enqueueing template queue is "+elapsedTime+" millisecond");
            } catch (InterruptedException e) {
                ILLUMINATI_TEMPLATE_EXECUTOR_LOGGER.error("Failed to enqueuing the template queue.. ("+e.getMessage()+")");
            }
        }
    }

    /**
     * only execute at debug
     * @param illuminatiTemplateInterfaceModel
     */
    private void sendToNextStepByDebug (final IlluminatiTemplateInterfaceModel illuminatiTemplateInterfaceModel) {
        // something to check validation.. but.. now not exists.
        if (ILLUMINATI_TEMPLATE == null) {
            ILLUMINATI_TEMPLATE_EXECUTOR_LOGGER.warn("ILLUMINATI_TEMPLATE is must not null.");
            return;
        }
        // debug illuminati rabbitmq queue
        if (IlluminatiConstant.ILLUMINATI_DEBUG == true) {
            final long start = System.currentTimeMillis();
            //## sendToIlluminati
            ILLUMINATI_TEMPLATE.sendToIlluminati(illuminatiTemplateInterfaceModel.getJsonString());
            final long elapsedTime = System.currentTimeMillis() - start;
            ILLUMINATI_TEMPLATE_EXECUTOR_LOGGER.info("template queue current size is "+String.valueOf(this.getQueueSize()));
            ILLUMINATI_TEMPLATE_EXECUTOR_LOGGER.info("elapsed time of Illuminati sent is "+elapsedTime+" millisecond");

            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                // ignore
            }
        }
    }

    @Override public IlluminatiTemplateInterfaceModel deQueueByDebug () {
        ILLUMINATI_TEMPLATE_EXECUTOR_LOGGER.info("template queue current size is "+String.valueOf(this.getQueueSize()));

        if (ILLUMINATI_MODEL_BLOCKING_QUEUE == null || this.getQueueSize() == 0) {
            return null;
        }
        try {
            final long start = System.currentTimeMillis();
            IlluminatiTemplateInterfaceModel illuminatiTemplateInterfaceModel = ILLUMINATI_MODEL_BLOCKING_QUEUE.poll(ILLUMINATI_DEQUEUING_TIMEOUT_MS, TimeUnit.MILLISECONDS);
            final long elapsedTime = System.currentTimeMillis() - start;
            ILLUMINATI_TEMPLATE_EXECUTOR_LOGGER.info("template queue after inserted size is "+String.valueOf(this.getQueueSize()));
            ILLUMINATI_TEMPLATE_EXECUTOR_LOGGER.info("elapsed time of dequeueing template queue is "+elapsedTime+" millisecond");
            return illuminatiTemplateInterfaceModel;
        } catch (InterruptedException e) {
            ILLUMINATI_TEMPLATE_EXECUTOR_LOGGER.warn("Failed to dequeing the rabbitmq queue.. ("+e.getMessage()+")");
        }

        return null;
    }

    private void createDebugThread () {
        // debug illuminati buffer queue
        if (IlluminatiConstant.ILLUMINATI_DEBUG == true) {
            final Runnable queueCheckRunnable = new Runnable() {
                public void run() {
                    while (true) {
                        ILLUMINATI_TEMPLATE_EXECUTOR_LOGGER.info("");
                        ILLUMINATI_TEMPLATE_EXECUTOR_LOGGER.info("#########################################################################################################");
                        ILLUMINATI_TEMPLATE_EXECUTOR_LOGGER.info("## template queue buffer debug info");
                        ILLUMINATI_TEMPLATE_EXECUTOR_LOGGER.info("## -------------------------------------------------------------------------------------------------------");
                        ILLUMINATI_TEMPLATE_EXECUTOR_LOGGER.info("## current template queue count : "+String.valueOf(getQueueSize()));
                        ILLUMINATI_TEMPLATE_EXECUTOR_LOGGER.info("#########################################################################################################");

                        try {
                            Thread.sleep(15000);
                        } catch (InterruptedException e) {
                            // ignore
                        }
                    }
                }
            };

            SystemUtil.createSystemThread(queueCheckRunnable, "ILLUMINATI_TEMPLATE_QUEUE_CHECK_THREAD");
        }
    }
}