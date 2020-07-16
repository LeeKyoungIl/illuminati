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

import me.phoboslabs.illuminati.common.constant.IlluminatiConstant;
import me.phoboslabs.illuminati.common.dto.ServerInfo;
import me.phoboslabs.illuminati.common.dto.impl.IlluminatiDataSendModel;
import me.phoboslabs.illuminati.common.dto.impl.IlluminatiBasicModel;
import me.phoboslabs.illuminati.common.properties.IlluminatiPropertiesHelper;
import me.phoboslabs.illuminati.common.util.SystemUtil;
import me.phoboslabs.illuminati.processor.exception.PublishMessageException;
import me.phoboslabs.illuminati.processor.executor.IlluminatiBasicExecutor;
import me.phoboslabs.illuminati.processor.executor.queue.IlluminatiBlockingQueue;
import me.phoboslabs.illuminati.processor.infra.IlluminatiInfraTemplate;
import me.phoboslabs.illuminati.processor.infra.common.IlluminatiInfraConstant;
import me.phoboslabs.illuminati.processor.infra.kafka.impl.KafkaInfraTemplateImpl;
import me.phoboslabs.illuminati.processor.infra.rabbitmq.impl.RabbitmqInfraTemplateImpl;
import me.phoboslabs.illuminati.processor.properties.IlluminatiPropertiesImpl;

import java.util.Map;

/**
 * Created by leekyoungil (leekyoungil@gmail.com) on 12/01/2017.
 */
public class IlluminatiDataSendExecutorImpl extends IlluminatiBasicExecutor<IlluminatiBasicModel> {

    private static final class IlluminatiDataSendExecutorImplHolder {
        private static final IlluminatiDataSendExecutorImpl INSTANCE_HOLDER = new IlluminatiDataSendExecutorImpl();
    }

    // ################################################################################################################
    // ### init illuminati data queue                                                                               ###
    // ################################################################################################################
    private static final int POLL_PER_COUNT = 1;
    private static final long ILLUMINATI_DATA_ENQUEUING_TIMEOUT_MS = 0L;

    // ################################################################################################################
    // ### init illuminati infra send executor                                                                      ###
    // ################################################################################################################
    private IlluminatiInfraTemplate illuminatiInfraTemplate;
    private final long BROKER_HEALTH_CHECK_TIME = 300000L;

    // ################################################################################################################
    // ### init illuminati basic system variables                                                                   ###
    // ################################################################################################################
    private final static String PARENT_MODULE_NAME = IlluminatiPropertiesHelper.getPropertiesValueByKey(IlluminatiPropertiesImpl.class, "illuminati", "parentModuleName", "no Name");
    private final static ServerInfo SERVER_INFO = new ServerInfo(true);
    // get basic JVM setting info only once.
    private final static Map<String, Object> JVM_INFO = SystemUtil.getJvmInfo();

    private IlluminatiDataSendExecutorImpl() {
        super(ILLUMINATI_DATA_ENQUEUING_TIMEOUT_MS, new IlluminatiBlockingQueue<>(ILLUMINATI_BAK_LOG, POLL_PER_COUNT));
    }

    // ################################################################################################################
    // ### public methods                                                                                           ###
    // ################################################################################################################

    public static IlluminatiDataSendExecutorImpl getInstance () {
        return IlluminatiDataSendExecutorImplHolder.INSTANCE_HOLDER;
    }

    @Override public synchronized IlluminatiDataSendExecutorImpl init () throws Exception {
        // create illuminati template queue thread for send to the IlluminatiDataInterfaceModelImpl.
        this.initIlluminatiInfraTemplate();

        this.createSystemThread();
        this.createSystemThreadForIsCanConnectRemoteBroker();

        if (IlluminatiConstant.ILLUMINATI_BACKUP_ACTIVATION) {
//            this.addShutdownHook();
        }

        return this;
    }

    // ################################################################################################################
    // ### private methods                                                                                          ###
    // ################################################################################################################

    private void addShutdownHook () {
//        Runtime.getRuntime().addShutdownHook(new ContainerSignalHandler(new IlluminatiShutdownHandler(this)));
    }

    private void initIlluminatiInfraTemplate () throws Exception {
        final String illuminatiBroker = IlluminatiPropertiesHelper.getPropertiesValueByKey(IlluminatiPropertiesImpl.class,  "illuminati", "broker", "no broker");

        try {
            switch (illuminatiBroker) {
                case "kafka" :
                    this.illuminatiInfraTemplate = new KafkaInfraTemplateImpl("illuminati");
                    break;
                case "rabbitmq" :
                    this.illuminatiInfraTemplate = new RabbitmqInfraTemplateImpl("illuminati");
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

        IlluminatiInfraConstant.setIsCanConnectToRemoteBroker(illuminatiInfraTemplate.canIConnect());

        if (!IlluminatiInfraConstant.isCanConnectToRemoteBroker()) {
            throw new Exception("Check your message broker.");
        }
    }

    private void addDataOnIlluminatiModel (final IlluminatiBasicModel illuminatiTemplateInterfaceModelImpl) {
        illuminatiTemplateInterfaceModelImpl.initStaticInfo(PARENT_MODULE_NAME, SERVER_INFO)
                .initBasicJvmInfo(JVM_INFO)
                .addBasicJvmMemoryInfo(SystemUtil.getJvmMemoryInfo())
                .setJavascriptUserAction();
    }

    @Override public void sendToNextStep(final IlluminatiDataSendModel illuminatiDataSendModel) {
        final IlluminatiBasicModel illuminatiTemplateInterfaceModelImpl = new IlluminatiBasicModel(illuminatiDataSendModel);

        try {
            this.addDataOnIlluminatiModel(illuminatiTemplateInterfaceModelImpl);

            //if (IlluminatiGracefulShutdownChecker.getIlluminatiReadyToShutdown() == false) {
                if (IlluminatiInfraConstant.isCanConnectToRemoteBroker()) {
                    this.illuminatiInfraTemplate.sendToIlluminati(illuminatiTemplateInterfaceModelImpl.getJsonString());
                } else {
                    ILLUMINATI_EXECUTOR_LOGGER.error("Check your message broker status. ");
                    ILLUMINATI_EXECUTOR_LOGGER.error("Your data sent to the Backup store.");
                }
            //} else {

            //}
        } catch (Exception | PublishMessageException ex) {
            ILLUMINATI_EXECUTOR_LOGGER.debug("error : check your broker. ("+ex.toString()+")");
            this.preventErrorOfSystemThread(illuminatiDataSendModel);
        }
    }

    @Override public void sendToNextStepByDebug (final IlluminatiDataSendModel illuminatiDataInterfaceModel) {
//        final long start = System.currentTimeMillis();
//        this.sendToNextStep(illuminatiDataInterfaceModelImpl);
//        final long elapsedTime = System.currentTimeMillis() - start;
//        ILLUMINATI_EXECUTOR_LOGGER.info("data queue current size is {}", this.getQueueSize());
//        ILLUMINATI_EXECUTOR_LOGGER.info("elapsed time of template queue sent is {} millisecond", elapsedTime);
    }

    @Override protected void preventErrorOfSystemThread(IlluminatiDataSendModel illuminatiDataInterfaceModel) {
////        if (this.illuminatiBackupExecutor == null) {
////            return;
////        }
////        IlluminatiInfraConstant.IS_CAN_CONNECT_TO_REMOTE_BROKER.lazySet(illuminatiTemplate.canIConnect());
////        this.illuminatiBackupExecutor.addToQueue(illuminatiTemplateInterfaceModelImpl);
    }

    private void createSystemThreadForIsCanConnectRemoteBroker () {
        final Runnable runnableFirst = () -> {
            while (true) {
                try {
                    IlluminatiInfraConstant.setIsCanConnectToRemoteBroker(illuminatiInfraTemplate.canIConnect());

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
