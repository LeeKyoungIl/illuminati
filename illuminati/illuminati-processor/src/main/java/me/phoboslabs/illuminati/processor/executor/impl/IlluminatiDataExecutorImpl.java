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
import me.phoboslabs.illuminati.processor.executor.IlluminatiBasicExecutor;
import me.phoboslabs.illuminati.processor.executor.IlluminatiBlockingQueue;
import me.phoboslabs.illuminati.processor.executor.IlluminatiExecutor;
import me.phoboslabs.illuminati.processor.infra.IlluminatiInfraTemplate;
import me.phoboslabs.illuminati.processor.infra.common.IlluminatiInfraConstant;
import me.phoboslabs.illuminati.processor.infra.kafka.impl.KafkaInfraTemplateImpl;
import me.phoboslabs.illuminati.processor.infra.rabbitmq.impl.RabbitmqInfraTemplateImpl;
import me.phoboslabs.illuminati.processor.properties.IlluminatiPropertiesImpl;
import me.phoboslabs.illuminati.common.dto.impl.IlluminatiDataInterfaceModelImpl;
import me.phoboslabs.illuminati.common.dto.impl.IlluminatiTemplateInterfaceModelImpl;
import me.phoboslabs.illuminati.common.dto.ServerInfo;
import me.phoboslabs.illuminati.common.properties.IlluminatiPropertiesHelper;
import me.phoboslabs.illuminati.common.util.SystemUtil;

import java.util.Map;

/**
 * Created by leekyoungil (leekyoungil@gmail.com) on 12/01/2017.
 */
public class IlluminatiDataExecutorImpl extends IlluminatiBasicExecutor<IlluminatiTemplateInterfaceModelImpl> {

    private static IlluminatiDataExecutorImpl ILLUMINATI_DATA_EXECUTOR_IMPL;

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

    private IlluminatiDataExecutorImpl () {
        super(ILLUMINATI_DATA_ENQUEUING_TIMEOUT_MS, new IlluminatiBlockingQueue<>(ILLUMINATI_BAK_LOG, POLL_PER_COUNT));
    }

    public static IlluminatiDataExecutorImpl getInstance () {
        if (ILLUMINATI_DATA_EXECUTOR_IMPL == null) {
            synchronized (IlluminatiDataExecutorImpl.class) {
                if (ILLUMINATI_DATA_EXECUTOR_IMPL == null) {
                    ILLUMINATI_DATA_EXECUTOR_IMPL = new IlluminatiDataExecutorImpl();
                }
            }
        }

        return ILLUMINATI_DATA_EXECUTOR_IMPL;
    }

    @Override public synchronized IlluminatiDataExecutorImpl init () throws Exception {
        // create illuminati template queue thread for send to the IlluminatiDataInterfaceModelImpl.
        this.initIlluminatiInfraTemplate();

        this.createSystemThread();
        this.createSystemThreadForIsCanConnectRemoteBroker();

        return this;
    }

    // ################################################################################################################
    // ### public methods                                                                                           ###
    // ################################################################################################################

//    @Override public void sendToNextStep(final IlluminatiTemplateInterfaceModelImpl illuminatiTemplateInterfaceModel) {
//        if (!illuminatiDataInterfaceModelImpl.isValid()) {
//            ILLUMINATI_EXECUTOR_LOGGER.warn("illuminatiDataInterfaceModelImpl is not valid");
//            return;
//        }
//        //## send To Illuminati template queue
//        this.sendToIlluminatiTemplateQueue(illuminatiDataInterfaceModelImpl);
//    }

    // ################################################################################################################
    // ### private methods                                                                                          ###
    // ################################################################################################################

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

    private void addDataOnIlluminatiModel (final IlluminatiTemplateInterfaceModelImpl illuminatiTemplateInterfaceModelImpl) {
        illuminatiTemplateInterfaceModelImpl.initStaticInfo(PARENT_MODULE_NAME, SERVER_INFO)
                .initBasicJvmInfo(JVM_INFO)
                .addBasicJvmMemoryInfo(SystemUtil.getJvmMemoryInfo())
                .setJavascriptUserAction();
    }

    @Override public void sendToNextStep(final IlluminatiDataInterfaceModelImpl illuminatiDataInterfaceModelImpl) {
        final IlluminatiTemplateInterfaceModelImpl illuminatiTemplateInterfaceModelImpl = new IlluminatiTemplateInterfaceModelImpl(illuminatiDataInterfaceModelImpl);

        try {
            this.addDataOnIlluminatiModel(illuminatiTemplateInterfaceModelImpl);

            if (IlluminatiInfraConstant.isCanConnectToRemoteBroker()) {
                this.illuminatiInfraTemplate.sendToIlluminati(illuminatiTemplateInterfaceModelImpl.getJsonString());
            } else {
                ILLUMINATI_EXECUTOR_LOGGER.error("Check your message broker status. ");
                ILLUMINATI_EXECUTOR_LOGGER.error("Your data sent to the Backup store.");
            }
        } catch (Exception | PublishMessageException ex) {
            ILLUMINATI_EXECUTOR_LOGGER.debug("error : check your broker. ("+ex.toString()+")");
            this.preventErrorOfSystemThread(illuminatiDataInterfaceModelImpl);
        }
    }

    @Override public void sendToNextStepByDebug (final IlluminatiDataInterfaceModelImpl illuminatiDataInterfaceModel) {
//        final long start = System.currentTimeMillis();
//        this.sendToNextStep(illuminatiDataInterfaceModelImpl);
//        final long elapsedTime = System.currentTimeMillis() - start;
//        ILLUMINATI_EXECUTOR_LOGGER.info("data queue current size is {}", this.getQueueSize());
//        ILLUMINATI_EXECUTOR_LOGGER.info("elapsed time of template queue sent is {} millisecond", elapsedTime);
    }

    @Override protected void preventErrorOfSystemThread(IlluminatiDataInterfaceModelImpl illuminatiDataInterfaceModel) {

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
