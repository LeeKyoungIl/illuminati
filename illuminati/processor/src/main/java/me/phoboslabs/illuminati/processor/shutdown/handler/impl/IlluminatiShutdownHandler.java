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

package me.phoboslabs.illuminati.processor.shutdown.handler.impl;

import me.phoboslabs.illuminati.processor.executor.impl.IlluminatiTemplateExecutorImpl;
import me.phoboslabs.illuminati.processor.shutdown.handler.ContainerShutdownHandler;

/**
 * - @marcus.moon provided me with an Graceful idea.
 * <p>
 * Created by leekyoungil (leekyoungil@gmail.com) on 04/05/2018.
 */
public class IlluminatiShutdownHandler implements ContainerShutdownHandler {

    // ################################################################################################################
    // ### init illuminati template executor                                                                        ###
    // ################################################################################################################
    private IlluminatiTemplateExecutorImpl illuminatiTemplateExecutor;

    public IlluminatiShutdownHandler(IlluminatiTemplateExecutorImpl illuminatiExecutor) {
        this.illuminatiTemplateExecutor = illuminatiExecutor;
    }

    @Override
    public boolean isRunning() {
        int templateQueueSize = this.illuminatiTemplateExecutor.getQueueSize();
        int backupQueueSize = this.illuminatiTemplateExecutor.getBackupQueueSize();

        return templateQueueSize > 0 && backupQueueSize > 0;
    }

    @Override
    public void stop() {
        this.illuminatiTemplateExecutor.connectionClose();
    }

    @Override
    public void stopSignal() {
        this.illuminatiTemplateExecutor.executeStopThread();
    }
}
