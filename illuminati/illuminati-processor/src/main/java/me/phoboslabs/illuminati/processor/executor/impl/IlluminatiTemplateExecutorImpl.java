/*
 * Copyright 2017 Phoboslabs.me
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package me.phoboslabs.illuminati.processor.executor.impl;

import me.phoboslabs.illuminati.processor.exception.PublishMessageException;
import me.phoboslabs.illuminati.processor.exception.RequiredValueException;
import me.phoboslabs.illuminati.processor.executor.IlluminatiBasicExecutor;
import me.phoboslabs.illuminati.processor.executor.IlluminatiBlockingQueue;
import me.phoboslabs.illuminati.processor.infra.IlluminatiInfraTemplate;
import me.phoboslabs.illuminati.processor.infra.simple.impl.SimpleInfraTemplateImpl;
import me.phoboslabs.illuminati.processor.shutdown.ContainerSignalHandler;
import me.phoboslabs.illuminati.processor.shutdown.IlluminatiGracefulShutdownChecker;
import me.phoboslabs.illuminati.processor.shutdown.handler.impl.IlluminatiShutdownHandler;
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

    @Override public synchronized IlluminatiTemplateExecutorImpl init () {
        if (this.illuminatiTemplate == null) {
            throw new RequiredValueException();
        }
        this.createSystemThread();
        this.createSystemThreadForIsCanConnectRemoteBroker();

        if (IlluminatiConstant.ILLUMINATI_BACKUP_ACTIVATION) {
            this.addShutdownHook();
        }

        return this;
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

    public void sendToIlluminati (final String jsonString) throws Exception, PublishMessageException {
        this.illuminatiTemplate.sendToIlluminati(jsonString);
    }

    @Override public void sendToNextStep(final IlluminatiTemplateInterfaceModelImpl illuminatiTemplateInterfaceModelImpl) throws Exception {
        // something to check validation.. but.. now not exists.
        if (this.illuminatiTemplate == null) {
            ILLUMINATI_EXECUTOR_LOGGER.warn("ILLUMINATI_TEMPLATE is must not null.");
            return;
        }
        if (!IlluminatiGracefulShutdownChecker.getIlluminatiReadyToShutdown()) {
            try {
                this.sendToIlluminati(illuminatiTemplateInterfaceModelImpl.getJsonString());
            } catch (Exception | PublishMessageException ex) {
                this.preventErrorOfSystemThread(illuminatiTemplateInterfaceModelImpl);
            }
        } else {
            this.preventErrorOfSystemThread(illuminatiTemplateInterfaceModelImpl);
        }
    }

    /**
     * only execute at debug
     * @param illuminatiTemplateInterfaceModelImpl - input parameter data model
     */
    @Override public void sendToNextStepByDebug (final IlluminatiTemplateInterfaceModelImpl illuminatiTemplateInterfaceModelImpl) throws Exception {
        if (!IlluminatiConstant.ILLUMINATI_DEBUG) {
            return;
        }
        // debug illuminati rabbitmq queue
        final long start = System.currentTimeMillis();
        this.sendToNextStep(illuminatiTemplateInterfaceModelImpl);
        final long elapsedTime = System.currentTimeMillis() - start;
        ILLUMINATI_EXECUTOR_LOGGER.info("template queue current size is {}", this.getQueueSize());
        ILLUMINATI_EXECUTOR_LOGGER.info("elapsed time of Illuminati sent is {} millisecond", elapsedTime);

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
        final String illuminatiBroker = IlluminatiPropertiesHelper.getPropertiesValueByKey(IlluminatiPropertiesImpl.class,  "illuminati", "broker", "simple");
        IlluminatiInfraTemplate illuminatiInfraTemplate;

        try {
            switch (illuminatiBroker) {
                case "kafka" :
                    illuminatiInfraTemplate = new KafkaInfraTemplateImpl("illuminati");
                    break;
                case "rabbitmq" :
                    illuminatiInfraTemplate = new RabbitmqInfraTemplateImpl("illuminati");
                    break;
                case "simple" :
                    illuminatiInfraTemplate = new SimpleInfraTemplateImpl("illuminati");
                    break;
                default :
                    final String errorMessage = "Sorry. check your properties of Illuminati";
                    throw new Exception(errorMessage);
            }
        } catch (Exception ex) {
            final String errorMessage = ex.getMessage();
            ILLUMINATI_EXECUTOR_LOGGER.warn(errorMessage);
            throw new Exception(errorMessage);
        }

        IlluminatiInfraConstant.IS_CAN_CONNECT_TO_REMOTE_BROKER.set(illuminatiInfraTemplate.canIConnect());

        if (!IlluminatiInfraConstant.IS_CAN_CONNECT_TO_REMOTE_BROKER.get()) {
            throw new Exception("Check your message broker.");
        }

        return illuminatiInfraTemplate;
    }

    @Override protected void preventErrorOfSystemThread(final IlluminatiTemplateInterfaceModelImpl illuminatiTemplateInterfaceModelImpl) {
        if (this.illuminatiBackupExecutor == null) {
            return;
        }
        IlluminatiInfraConstant.IS_CAN_CONNECT_TO_REMOTE_BROKER.lazySet(illuminatiTemplate.canIConnect());
        this.illuminatiBackupExecutor.addToQueue(illuminatiTemplateInterfaceModelImpl);
    }

    private void createSystemThreadForIsCanConnectRemoteBroker () {
        final Runnable runnableFirst = () -> {
            while (true) {
                try {
                    IlluminatiInfraConstant.IS_CAN_CONNECT_TO_REMOTE_BROKER.lazySet(illuminatiTemplate.canIConnect());

                    try {
                        Thread.sleep(BROKER_HEALTH_CHECK_TIME);
                    } catch (InterruptedException ignore) {}
                } catch (Exception e) {
                    ILLUMINATI_EXECUTOR_LOGGER.warn("Failed to execute the ILLUMINATI_BROKER_HEALTH_CHECKER.. ({})", e.getMessage());
                }
            }
        };

        SystemUtil.createSystemThread(runnableFirst, this.getClass().getName() + " : ILLUMINATI_BROKER_HEALTH_CHECKER");
    }
}