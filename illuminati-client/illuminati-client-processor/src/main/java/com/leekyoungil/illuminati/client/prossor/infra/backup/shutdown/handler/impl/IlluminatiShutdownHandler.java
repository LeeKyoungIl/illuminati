package com.leekyoungil.illuminati.client.prossor.infra.backup.shutdown.handler.impl;

import com.leekyoungil.illuminati.client.prossor.executor.impl.IlluminatiTemplateExecutorImpl;
import com.leekyoungil.illuminati.client.prossor.infra.backup.shutdown.handler.ContainerShutdownHandler;

/**
 *  - i refer to the source that @marcus.moon created.
 */
public class IlluminatiShutdownHandler implements ContainerShutdownHandler {

    // ################################################################################################################
    // ### init illuminati template executor                                                                        ###
    // ################################################################################################################
    private IlluminatiTemplateExecutorImpl illuminatiTemplateExecutor;

    public IlluminatiShutdownHandler (IlluminatiTemplateExecutorImpl illuminatiExecutor) {
        this.illuminatiTemplateExecutor = illuminatiExecutor;
    }

    @Override public boolean isRunning() {
        int templateQueueSize = this.illuminatiTemplateExecutor.getQueueSize();
        int backupQueueSize = this.illuminatiTemplateExecutor.getBackupQueueSize();

        return templateQueueSize == 0 && backupQueueSize == 0 ? false : true;
    }

    @Override public void stop() {
        this.illuminatiTemplateExecutor.connectionClose();
    }

    @Override public void stopSignal() {
        this.illuminatiTemplateExecutor.executeStopThread();
    }
}
