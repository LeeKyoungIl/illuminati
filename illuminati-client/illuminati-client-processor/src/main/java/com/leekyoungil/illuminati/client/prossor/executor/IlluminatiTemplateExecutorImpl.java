package com.leekyoungil.illuminati.client.prossor.executor;

import com.leekyoungil.illuminati.common.dto.IlluminatiDataInterfaceModel;
import com.leekyoungil.illuminati.common.dto.IlluminatiTemplateInterfaceModel;
import com.leekyoungil.illuminati.common.constant.IlluminatiConstant;
import com.leekyoungil.illuminati.common.util.SystemUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import static com.leekyoungil.illuminati.client.prossor.executor.IlluminatiDataExecutorImpl.ILLUMINATI_TEMPLATE;

public class IlluminatiTemplateExecutorImpl implements IlluminatiExecutor<IlluminatiTemplateInterfaceModel> {

    private static final Logger ILLUMINATI_TEMPLATE_EXECUTOR_LOGGER = LoggerFactory.getLogger(IlluminatiTemplateExecutorImpl.class);

    // ################################################################################################################
    // ### init illuminati data queue                                                                               ###
    // ################################################################################################################
    private static final int ILLUMINATI_BAK_LOG = 10000;
    private static final long ILLUMINATI_DEQUEUING_TIMEOUT_MS = 1000;
    private static final long ILLUMINATI_ENQUEUING_TIMEOUT_MS = 0;

    private final static BlockingQueue<IlluminatiTemplateInterfaceModel> ILLUMINATI_MODEL_BLOCKING_QUEUE = new LinkedBlockingQueue<IlluminatiTemplateInterfaceModel>(ILLUMINATI_BAK_LOG);

    @Override public synchronized void init () {
        final Runnable runnableFirst = new Runnable() {
            public void run() {
                while (true) {
                    try {
                        final IlluminatiTemplateInterfaceModel illuminatiTemplateInterfaceModel = ILLUMINATI_MODEL_BLOCKING_QUEUE.poll(ILLUMINATI_DEQUEUING_TIMEOUT_MS, TimeUnit.MILLISECONDS);
                        if (illuminatiTemplateInterfaceModel != null) {
                            if (IlluminatiConstant.ILLUMINATI_DEBUG == false) {
                                sendToNextStep(illuminatiTemplateInterfaceModel);
                            } else {
                                IlluminatiTemplateExecutorImpl.sendToIlluminatiByDebug(illuminatiTemplateInterfaceModel);
                            }
                        }
                    } catch (InterruptedException e) {
                        ILLUMINATI_TEMPLATE_EXECUTOR_LOGGER.warn("Failed to dequeing the rabbitmq queue.. ("+e.getMessage()+")");
                    } catch (Exception e) {
                        ILLUMINATI_TEMPLATE_EXECUTOR_LOGGER.warn("Failed to send the rabbitmq queue.. ("+e.getMessage()+")");
                    }
                }
            }
        };

        SystemUtil.createSystemThread(runnableFirst, "ILLUMINATI_SENDER_THREAD");

        IlluminatiTemplateExecutorImpl.createDebugThread();
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
            IlluminatiTemplateExecutorImpl.executeToIlluminatiByDebug(illuminatiTemplateInterfaceModel);
        }
    }

    @Override public int getQueueSize () {
        if (ILLUMINATI_MODEL_BLOCKING_QUEUE == null) {
            ILLUMINATI_TEMPLATE_EXECUTOR_LOGGER.warn("ILLUMINATI_DATA_BLOCKING_QUEUE is must not null.");
            return 0;
        }
        return ILLUMINATI_MODEL_BLOCKING_QUEUE.size();
    }

    @Override public IlluminatiTemplateInterfaceModel deQueue() {
        return null;
    }

    @Override public void sendToNextStep(IlluminatiTemplateInterfaceModel illuminatiTemplateInterfaceModel) {
        // something to check validation.. but.. now not exists.
        if (ILLUMINATI_TEMPLATE == null) {
            ILLUMINATI_TEMPLATE_EXECUTOR_LOGGER.warn("ILLUMINATI_TEMPLATE is must not null.");
            return;
        }
        ILLUMINATI_TEMPLATE.sendToIlluminati(illuminatiTemplateInterfaceModel.getJsonString());
    }

    // ################################################################################################################
    // ### private methods                                                                                          ###
    // ################################################################################################################

    /**
     * only execute at debug
     * @param illuminatiTemplateInterfaceModel
     */
    private static void executeToIlluminatiByDebug (final IlluminatiTemplateInterfaceModel illuminatiTemplateInterfaceModel) {
        // debug illuminati buffer queue
        if (IlluminatiConstant.ILLUMINATI_DEBUG == true) {
            try {
                final long start = System.currentTimeMillis();
                ILLUMINATI_MODEL_BLOCKING_QUEUE.offer(illuminatiTemplateInterfaceModel, ILLUMINATI_ENQUEUING_TIMEOUT_MS, TimeUnit.MILLISECONDS);
                final long elapsedTime = System.currentTimeMillis() - start;
                ILLUMINATI_TEMPLATE_EXECUTOR_LOGGER.info("elapsed time of enqueueing queue is "+elapsedTime+" millisecond");
            } catch (InterruptedException e) {
                ILLUMINATI_TEMPLATE_EXECUTOR_LOGGER.error("Failed to enqueuing the Illuminati queue.. ("+e.getMessage()+")");
            }
        }
    }

    /**
     * only execute at debug
     * @param illuminatiTemplateInterfaceModel
     */
    private static void sendToIlluminatiByDebug (final IlluminatiTemplateInterfaceModel illuminatiTemplateInterfaceModel) {
        // debug illuminati rabbitmq queue
        if (IlluminatiConstant.ILLUMINATI_DEBUG == true) {
            final long start = System.currentTimeMillis();
            //## sendToIlluminati
            ILLUMINATI_TEMPLATE.sendToIlluminati(illuminatiTemplateInterfaceModel.getJsonString());
            final long elapsedTime = System.currentTimeMillis() - start;
            ILLUMINATI_TEMPLATE_EXECUTOR_LOGGER.info("elapsed time of Illuminati sent is "+elapsedTime+" millisecond");
        }
    }

    private static void createDebugThread () {
        // debug illuminati buffer queue
        if (IlluminatiConstant.ILLUMINATI_DEBUG == true) {
            final Runnable queueCheckRunnable = new Runnable() {
                public void run() {
                    while (true) {
                        ILLUMINATI_TEMPLATE_EXECUTOR_LOGGER.info("");
                        ILLUMINATI_TEMPLATE_EXECUTOR_LOGGER.info("#########################################################################################################");
                        ILLUMINATI_TEMPLATE_EXECUTOR_LOGGER.info("# queue buffer debug info");
                        ILLUMINATI_TEMPLATE_EXECUTOR_LOGGER.info("# -------------------------------------------------------------------------------------------------------");
                        ILLUMINATI_TEMPLATE_EXECUTOR_LOGGER.info("# current queue count : "+ILLUMINATI_MODEL_BLOCKING_QUEUE.size());
                        ILLUMINATI_TEMPLATE_EXECUTOR_LOGGER.info("#########################################################################################################");

                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            // ignore
                        }
                    }
                }
            };

            SystemUtil.createSystemThread(queueCheckRunnable, "ILLUMINATI_QUEUE_CHECK_THREAD");
        }
    }
}