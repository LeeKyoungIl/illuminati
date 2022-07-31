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

import java.util.Map;
import me.phoboslabs.illuminati.common.dto.ServerInfo;
import me.phoboslabs.illuminati.common.dto.impl.IlluminatiDataInterfaceModelImpl;
import me.phoboslabs.illuminati.common.dto.impl.IlluminatiTemplateInterfaceModelImpl;
import me.phoboslabs.illuminati.common.properties.IlluminatiPropertiesHelper;
import me.phoboslabs.illuminati.common.util.SystemUtil;
import me.phoboslabs.illuminati.processor.executor.IlluminatiBasicExecutor;
import me.phoboslabs.illuminati.processor.executor.IlluminatiBlockingQueue;
import me.phoboslabs.illuminati.processor.executor.IlluminatiExecutor;
import me.phoboslabs.illuminati.processor.properties.IlluminatiPropertiesImpl;

/**
 * Created by leekyoungil (leekyoungil@gmail.com) on 12/01/2017.
 */
public class IlluminatiDataExecutorImpl extends IlluminatiBasicExecutor<IlluminatiDataInterfaceModelImpl> {

    private static IlluminatiDataExecutorImpl ILLUMINATI_DATA_EXECUTOR_IMPL;

    // ################################################################################################################
    // ### init illuminati data queue                                                                               ###
    // ################################################################################################################
    private static final int POLL_PER_COUNT = 1;
    private static final long ILLUMINATI_DATA_ENQUEUING_TIMEOUT_MS = 0L;

    // ################################################################################################################
    // ### init illuminati template executor                                                                        ###
    // ################################################################################################################
    private IlluminatiExecutor<IlluminatiTemplateInterfaceModelImpl> illuminatiTemplateExecutor;

    // ################################################################################################################
    // ### init illuminati basic system variables                                                                   ###
    // ################################################################################################################
    private final static String PARENT_MODULE_NAME = IlluminatiPropertiesHelper.getPropertiesValueByKey(
        IlluminatiPropertiesImpl.class, "illuminati", "parentModuleName", "no Name");
    private final static ServerInfo SERVER_INFO = new ServerInfo(true);
    // get basic JVM setting info only once.
    private final static Map<String, Object> JVM_INFO = SystemUtil.getJvmInfo();

    private IlluminatiDataExecutorImpl(IlluminatiExecutor illuminatiExecutor) {
        super(ILLUMINATI_DATA_ENQUEUING_TIMEOUT_MS, new IlluminatiBlockingQueue<>(ILLUMINATI_BAK_LOG, POLL_PER_COUNT));
        this.illuminatiTemplateExecutor = illuminatiExecutor;
    }

    public static IlluminatiDataExecutorImpl getInstance(IlluminatiExecutor illuminatiExecutor) {
        if (ILLUMINATI_DATA_EXECUTOR_IMPL == null) {
            synchronized (IlluminatiDataExecutorImpl.class) {
                if (ILLUMINATI_DATA_EXECUTOR_IMPL == null) {
                    ILLUMINATI_DATA_EXECUTOR_IMPL = new IlluminatiDataExecutorImpl(illuminatiExecutor);
                }
            }
        }

        return ILLUMINATI_DATA_EXECUTOR_IMPL;
    }

    @Override
    public synchronized IlluminatiDataExecutorImpl init() {
        // create illuminati template queue thread for send to the IlluminatiDataInterfaceModelImpl.
        this.createSystemThread();

        return this;
    }

    // ################################################################################################################
    // ### public methods                                                                                           ###
    // ################################################################################################################

    @Override
    public void sendToNextStep(IlluminatiDataInterfaceModelImpl illuminatiDataInterfaceModelImpl) {
        if (!illuminatiDataInterfaceModelImpl.isValid()) {
            ILLUMINATI_EXECUTOR_LOGGER.warn("illuminatiDataInterfaceModelImpl is not valid");
            return;
        }
        //## send To Illuminati template queue
        this.sendToIlluminatiTemplateQueue(illuminatiDataInterfaceModelImpl);
    }

    // ################################################################################################################
    // ### private methods                                                                                          ###
    // ################################################################################################################

    private void addDataOnIlluminatiModel(IlluminatiTemplateInterfaceModelImpl illuminatiTemplateInterfaceModelImpl) {
        illuminatiTemplateInterfaceModelImpl.initStaticInfo(PARENT_MODULE_NAME, SERVER_INFO)
            .initBasicJvmInfo(JVM_INFO)
            .addBasicJvmMemoryInfo(SystemUtil.getJvmMemoryInfo())
            .setJavascriptUserAction();
    }

    private void sendToIlluminatiTemplateQueue(IlluminatiDataInterfaceModelImpl illuminatiDataInterfaceModelImpl) {
        try {
            final IlluminatiTemplateInterfaceModelImpl illuminatiTemplateInterfaceModelImpl = new IlluminatiTemplateInterfaceModelImpl(
                illuminatiDataInterfaceModelImpl);
            this.addDataOnIlluminatiModel(illuminatiTemplateInterfaceModelImpl);
            this.illuminatiTemplateExecutor.addToQueue(illuminatiTemplateInterfaceModelImpl);
        } catch (Exception ex) {
            ILLUMINATI_EXECUTOR_LOGGER.debug("error : check your broker. (" + ex.toString() + ")");
        }
    }

    @Override
    public void sendToNextStepByDebug(IlluminatiDataInterfaceModelImpl illuminatiDataInterfaceModelImpl) {
        final long start = System.currentTimeMillis();
        this.sendToNextStep(illuminatiDataInterfaceModelImpl);
        final long elapsedTime = System.currentTimeMillis() - start;
        ILLUMINATI_EXECUTOR_LOGGER.info("data queue current size is {}", this.getQueueSize());
        ILLUMINATI_EXECUTOR_LOGGER.info("elapsed time of template queue sent is {} millisecond", elapsedTime);
    }

    @Override
    protected void preventErrorOfSystemThread(IlluminatiDataInterfaceModelImpl illuminatiDataInterfaceModel) {

    }
}
