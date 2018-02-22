package com.leekyoungil.illuminati.client.prossor.executor.impl;

import com.leekyoungil.illuminati.client.prossor.executor.IlluminatiBasicExecutor;
import com.leekyoungil.illuminati.client.prossor.executor.IlluminatiExecutor;
import com.leekyoungil.illuminati.client.prossor.infra.IlluminatiInfraTemplate;
import com.leekyoungil.illuminati.client.prossor.infra.kafka.impl.KafkaInfraTemplateImpl;
import com.leekyoungil.illuminati.client.prossor.infra.rabbitmq.impl.RabbitmqInfraTemplateImpl;
import com.leekyoungil.illuminati.client.prossor.properties.IlluminatiPropertiesImpl;
import com.leekyoungil.illuminati.common.constant.IlluminatiConstant;
import com.leekyoungil.illuminati.common.dto.impl.IlluminatiTemplateInterfaceModelImpl;
import com.leekyoungil.illuminati.common.properties.IlluminatiPropertiesHelper;

/**
 * Created by leekyoungil (leekyoungil@gmail.com) on 12/01/2017.
 */
public class IlluminatiTemplateExecutorImpl extends IlluminatiBasicExecutor<IlluminatiTemplateInterfaceModelImpl> {

    private static IlluminatiTemplateExecutorImpl ILLUMINATI_TEMPLATE_EXECUTOR_IMPL;

    // ################################################################################################################
    // ### init illuminati template queue                                                                           ###
    // ################################################################################################################
    private static final int ILLUMINATI_BAK_LOG = 10000;
    private static final long ILLUMINATI_DEQUEUING_TIMEOUT_MS = 1000l;
    private static final long ILLUMINATI_ENQUEUING_TIMEOUT_MS = 0l;

    // ################################################################################################################
    // ### init illuminati template executor                                                                        ###
    // ################################################################################################################
    private IlluminatiExecutor<IlluminatiTemplateInterfaceModelImpl> illuminatiBackupExecutor;

    // ################################################################################################################
    // ### init illuminati broker                                                                                   ###
    // ################################################################################################################
    private final IlluminatiInfraTemplate illuminatiTemplate = this.initIlluminatiTemplate();

    private IlluminatiTemplateExecutorImpl (final IlluminatiExecutor illuminatiBackupExecutor) {
        super(ILLUMINATI_BAK_LOG, ILLUMINATI_ENQUEUING_TIMEOUT_MS, ILLUMINATI_DEQUEUING_TIMEOUT_MS, new IlluminatiBlockingQueue<IlluminatiTemplateInterfaceModelImpl>());
        this.illuminatiBackupExecutor = illuminatiBackupExecutor;
    }

    public static IlluminatiTemplateExecutorImpl getInstance (final IlluminatiExecutor illuminatiBackupExecutor) {
        if (ILLUMINATI_TEMPLATE_EXECUTOR_IMPL == null) {
            synchronized (IlluminatiTemplateExecutorImpl.class) {
                if (ILLUMINATI_TEMPLATE_EXECUTOR_IMPL == null) {
                    ILLUMINATI_TEMPLATE_EXECUTOR_IMPL = new IlluminatiTemplateExecutorImpl(illuminatiBackupExecutor);
                }
            }
        }

        return ILLUMINATI_TEMPLATE_EXECUTOR_IMPL;
    }

    @Override public synchronized void init () {
        if (illuminatiTemplate != null) {
            this.createSystemThread();
        }
    }

    // ################################################################################################################
    // ### public methods                                                                                           ###
    // ################################################################################################################

    public void sendToIlluminati (final String jsonString) {
        illuminatiTemplate.sendToIlluminati(jsonString);
    }

    @Override public void sendToNextStep(final IlluminatiTemplateInterfaceModelImpl illuminatiTemplateInterfaceModelImpl) {
        // something to check validation.. but.. now not exists.
        if (illuminatiTemplate == null) {
            illuminatiExecutorLogger.warn("ILLUMINATI_TEMPLATE is must not null.");
            return;
        }
        this.sendToIlluminati(illuminatiTemplateInterfaceModelImpl.getJsonString());
    }

    /**
     * only execute at debug
     * @param illuminatiTemplateInterfaceModelImpl
     */
    @Override public void sendToNextStepByDebug (final IlluminatiTemplateInterfaceModelImpl illuminatiTemplateInterfaceModelImpl) {
        // debug illuminati rabbitmq queue
        if (IlluminatiConstant.ILLUMINATI_DEBUG == true) {
            final long start = System.currentTimeMillis();
            this.sendToNextStep(illuminatiTemplateInterfaceModelImpl);
            final long elapsedTime = System.currentTimeMillis() - start;
            illuminatiExecutorLogger.info("template queue current size is "+String.valueOf(this.getQueueSize()));
            illuminatiExecutorLogger.info("elapsed time of Illuminati sent is "+elapsedTime+" millisecond");

            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                // ignore
            }
        }
    }

    // ################################################################################################################
    // ### private methods                                                                                          ###
    // ################################################################################################################

    private IlluminatiInfraTemplate initIlluminatiTemplate () {
        final String illuminatiBroker = IlluminatiPropertiesHelper.getPropertiesValueByKey(IlluminatiPropertiesImpl.class, null, "illuminati", "broker", "no broker");
        IlluminatiInfraTemplate illuminatiInfraTemplate;

        if ("kafka".equals(illuminatiBroker)) {
            illuminatiInfraTemplate = new KafkaInfraTemplateImpl("illuminati");
        } else if ("rabbitmq".equals(illuminatiBroker)) {
            illuminatiInfraTemplate = new RabbitmqInfraTemplateImpl("illuminati");
        } else {
            illuminatiExecutorLogger.warn("Sorry. check your properties of Illuminati");
            return null;
        }

        if (illuminatiInfraTemplate == null) {
            return null;
        }

        return illuminatiInfraTemplate;
    }

    @Override protected void preventErrorOfSystemThread(final IlluminatiTemplateInterfaceModelImpl illuminatiTemplateInterfaceModelImpl) {
        this.illuminatiBackupExecutor.addToQueue(illuminatiTemplateInterfaceModelImpl);
    }
}