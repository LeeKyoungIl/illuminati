package me.phoboslabs.illuminati.processor.executor.impl;

import me.phoboslabs.illuminati.processor.executor.IlluminatiBasicExecutor;
import me.phoboslabs.illuminati.processor.executor.IlluminatiBlockingQueue;
import me.phoboslabs.illuminati.processor.infra.IlluminatiInfraTemplate;
import me.phoboslabs.illuminati.processor.infra.backup.shutdown.ContainerSignalHandler;
import me.phoboslabs.illuminati.processor.infra.backup.shutdown.IlluminatiGracefulShutdownChecker;
import me.phoboslabs.illuminati.processor.infra.backup.shutdown.handler.impl.IlluminatiShutdownHandler;
import me.phoboslabs.illuminati.processor.infra.common.IlluminatiInfraConstant;
import me.phoboslabs.illuminati.processor.infra.kafka.impl.KafkaInfraTemplateImpl;
import me.phoboslabs.illuminati.processor.infra.rabbitmq.impl.RabbitmqInfraTemplateImpl;
import me.phoboslabs.illuminati.processor.properties.IlluminatiPropertiesImpl;
import me.phoboslabs.illuminati.common.constant.IlluminatiConstant;
import me.phoboslabs.illuminati.common.dto.impl.IlluminatiTemplateInterfaceModelImpl;
import me.phoboslabs.illuminati.common.properties.IlluminatiPropertiesHelper;
import me.phoboslabs.illuminati.common.util.SystemUtil;

/**
 * Created by leekyoungil (leekyoungil@gmail.com) on 12/01/2017.
 */
public class IlluminatiTemplateExecutorImpl extends IlluminatiBasicExecutor<IlluminatiTemplateInterfaceModelImpl> {

    private static IlluminatiTemplateExecutorImpl ILLUMINATI_TEMPLATE_EXECUTOR_IMPL;

    // ################################################################################################################
    // ### init illuminati template queue                                                                           ###
    // ################################################################################################################
    private static final int POLL_PER_COUNT = 1;
    private static final long ILLUMINATI_ENQUEUING_TIMEOUT_MS = 0L;

    // ################################################################################################################
    // ### init illuminati backup executor                                                                        ###
    // ################################################################################################################
    private IlluminatiBackupExecutorImpl illuminatiBackupExecutor;

    // ################################################################################################################
    // ### init illuminati broker                                                                                   ###
    // ################################################################################################################
    private final IlluminatiInfraTemplate illuminatiTemplate;
    private final long BROKER_HEALTH_CHECK_TIME = 300000L;

    private IlluminatiShutdownHandler illuminatiShutdownHandler;

    private IlluminatiTemplateExecutorImpl (final IlluminatiBackupExecutorImpl illuminatiBackupExecutor) throws Exception {
        super(ILLUMINATI_ENQUEUING_TIMEOUT_MS, new IlluminatiBlockingQueue<>(ILLUMINATI_BAK_LOG, POLL_PER_COUNT));
        this.illuminatiBackupExecutor = illuminatiBackupExecutor;
        this.illuminatiTemplate = this.initIlluminatiTemplate();
    }

    public static IlluminatiTemplateExecutorImpl getInstance (final IlluminatiBackupExecutorImpl illuminatiBackupExecutor) throws Exception {
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
        if (this.illuminatiTemplate == null) {
            return;
        }
        this.createSystemThread();
        this.createSystemThreadForIsCanConnectRemoteBroker();

        if (IlluminatiConstant.ILLUMINATI_BACKUP_ACTIVATION) {
            this.addShutdownHook();
        }
    }

    // ################################################################################################################
    // ### public methods                                                                                           ###
    // ################################################################################################################

    public void connectionClose () {
        this.illuminatiTemplate.connectionClose();
    }
    public void executeStopThread () {
        this.illuminatiBackupExecutor.createStopThread();
    }
    public int getBackupQueueSize () {
        return this.illuminatiBackupExecutor.getQueueSize();
    }

    public void sendToIlluminati (final String jsonString) throws Exception {
        this.illuminatiTemplate.sendToIlluminati(jsonString);
    }

    @Override public void sendToNextStep(final IlluminatiTemplateInterfaceModelImpl illuminatiTemplateInterfaceModelImpl) throws Exception {
        // something to check validation.. but.. now not exists.
        if (this.illuminatiTemplate == null) {
            illuminatiExecutorLogger.warn("ILLUMINATI_TEMPLATE is must not null.");
            return;
        }
        if (IlluminatiGracefulShutdownChecker.getIlluminatiReadyToShutdown() == false) {
            this.sendToIlluminati(illuminatiTemplateInterfaceModelImpl.getJsonString());
        } else {
            this.preventErrorOfSystemThread(illuminatiTemplateInterfaceModelImpl);
        }
    }

    /**
     * only execute at debug
     * @param illuminatiTemplateInterfaceModelImpl
     */
    @Override public void sendToNextStepByDebug (final IlluminatiTemplateInterfaceModelImpl illuminatiTemplateInterfaceModelImpl) throws Exception {
        if (IlluminatiConstant.ILLUMINATI_DEBUG == false) {
            return;
        }
        // debug illuminati rabbitmq queue
        final long start = System.currentTimeMillis();
        this.sendToNextStep(illuminatiTemplateInterfaceModelImpl);
        final long elapsedTime = System.currentTimeMillis() - start;
        illuminatiExecutorLogger.info("template queue current size is "+String.valueOf(this.getQueueSize()));
        illuminatiExecutorLogger.info("elapsed time of Illuminati sent is "+elapsedTime+" millisecond");

        try {
            Thread.sleep(5000);
        } catch (InterruptedException ignore) {}
    }

    // ################################################################################################################
    // ### private methods                                                                                          ###
    // ################################################################################################################

    private void addShutdownHook () {
       Runtime.getRuntime().addShutdownHook(new ContainerSignalHandler(new IlluminatiShutdownHandler(this)));
    }

    private IlluminatiInfraTemplate initIlluminatiTemplate () throws Exception {
        final String illuminatiBroker = IlluminatiPropertiesHelper.getPropertiesValueByKey(IlluminatiPropertiesImpl.class,  "illuminati", "broker", "no broker");
        IlluminatiInfraTemplate illuminatiInfraTemplate;

        switch (illuminatiBroker) {
            case "kafka" :
                illuminatiInfraTemplate = new KafkaInfraTemplateImpl("illuminati");
                break;
            case "rabbitmq" :
                illuminatiInfraTemplate = new RabbitmqInfraTemplateImpl("illuminati");
                break;
            default :
                final String errorMessage = "Sorry. check your properties of Illuminati";
                illuminatiExecutorLogger.warn(errorMessage);
                throw new Exception(errorMessage);
        }

        IlluminatiInfraConstant.IS_CANCONNECT_TO_REMOTE_BROKER.set(illuminatiInfraTemplate.canIConnect());

        if (IlluminatiInfraConstant.IS_CANCONNECT_TO_REMOTE_BROKER.get() == false) {
            throw new Exception("Check your message broker.");
        }

        return illuminatiInfraTemplate;
    }

    @Override protected void preventErrorOfSystemThread(final IlluminatiTemplateInterfaceModelImpl illuminatiTemplateInterfaceModelImpl) {
        if (this.illuminatiBackupExecutor == null) {
            return;
        }
        IlluminatiInfraConstant.IS_CANCONNECT_TO_REMOTE_BROKER.lazySet(illuminatiTemplate.canIConnect());
        this.illuminatiBackupExecutor.addToQueue(illuminatiTemplateInterfaceModelImpl);
    }

    private void createSystemThreadForIsCanConnectRemoteBroker () {
        final Runnable runnableFirst = new Runnable() {
            public void run() {
                while (true) {
                    try {
                        IlluminatiInfraConstant.IS_CANCONNECT_TO_REMOTE_BROKER.lazySet(illuminatiTemplate.canIConnect());

                        try {
                            Thread.sleep(BROKER_HEALTH_CHECK_TIME);
                        } catch (InterruptedException ignore) {}
                    } catch (Exception e) {
                        illuminatiExecutorLogger.warn("Failed to execute the ILLUMINATI_BROKER_HEALTH_CHECKER.. ("+e.getMessage()+")");
                    }
                }
            }
        };

        SystemUtil.createSystemThread(runnableFirst, this.getClass().getName() + " : ILLUMINATI_BROKER_HEALTH_CHECKER");
    }
}