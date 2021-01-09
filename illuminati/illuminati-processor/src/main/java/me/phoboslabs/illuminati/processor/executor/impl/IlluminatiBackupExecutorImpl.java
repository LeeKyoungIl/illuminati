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

import me.phoboslabs.illuminati.processor.exception.RequiredValueException;
import me.phoboslabs.illuminati.processor.executor.IlluminatiBasicExecutor;
import me.phoboslabs.illuminati.processor.executor.IlluminatiBlockingQueue;
import me.phoboslabs.illuminati.processor.infra.h2.DBExecutor;
import me.phoboslabs.illuminati.processor.infra.backup.BackupFactory;
import me.phoboslabs.illuminati.processor.shutdown.IlluminatiGracefulShutdownChecker;
import me.phoboslabs.illuminati.common.constant.IlluminatiConstant;
import me.phoboslabs.illuminati.common.dto.IlluminatiInterfaceModel;
import me.phoboslabs.illuminati.common.dto.enums.IlluminatiInterfaceType;
import me.phoboslabs.illuminati.common.dto.impl.IlluminatiTemplateInterfaceModelImpl;
import me.phoboslabs.illuminati.common.util.SystemUtil;
import org.apache.commons.collections.CollectionUtils;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by leekyoungil (leekyoungil@gmail.com) on 04/05/2018.
 */
public class IlluminatiBackupExecutorImpl extends IlluminatiBasicExecutor<IlluminatiTemplateInterfaceModelImpl> {

    private static IlluminatiBackupExecutorImpl ILLUMINATI_BACKUP_EXECUTOR_IMPL;

    // ################################################################################################################
    // ### init illuminati file backup queue                                                                        ###
    // ################################################################################################################
    private static final int ILLUMINATI_BAK_LOG = 100000;
    private static final long ILLUMINATI_FILE_BACKUP_DEQUEUING_TIMEOUT_MS = 3000L;
    private static final long ILLUMINATI_FILE_BACKUP_ENQUEUING_TIMEOUT_MS = 3000L;

    // ################################################################################################################
    // ### init illuminati file backup queue                                                                        ###
    // ################################################################################################################
    private static final int POLL_PER_COUNT = 1000;
    private static final long BACKUP_THREAD_SLEEP_TIME = 300000L;

    private final DBExecutor<IlluminatiInterfaceModel> DBExecutor;

    private IlluminatiBackupExecutorImpl() throws Exception {
        super(ILLUMINATI_FILE_BACKUP_ENQUEUING_TIMEOUT_MS, new IlluminatiBlockingQueue<>(ILLUMINATI_BAK_LOG, POLL_PER_COUNT));
        this.DBExecutor = BackupFactory.getBackupInstance(IlluminatiConstant.ILLUMINATI_BACKUP_STORAGE_TYPE);
    }

    public static IlluminatiBackupExecutorImpl getInstance () throws Exception {
        if (ILLUMINATI_BACKUP_EXECUTOR_IMPL == null) {
            synchronized (IlluminatiBackupExecutorImpl.class) {
                if (ILLUMINATI_BACKUP_EXECUTOR_IMPL == null) {
                    ILLUMINATI_BACKUP_EXECUTOR_IMPL = new IlluminatiBackupExecutorImpl();
                }
            }
        }

        return ILLUMINATI_BACKUP_EXECUTOR_IMPL;
    }

    @Override public IlluminatiBackupExecutorImpl init() throws RequiredValueException {
        if (this.DBExecutor == null) {
            throw new RequiredValueException();
        }
        this.createSystemThread();

        return this;
    }

    @Override public IlluminatiTemplateInterfaceModelImpl deQueue() throws Exception {
        List<IlluminatiTemplateInterfaceModelImpl> backupObjectList = illuminatiBlockingQueue.pollToList(ILLUMINATI_FILE_BACKUP_DEQUEUING_TIMEOUT_MS, TimeUnit.MILLISECONDS);

        if (CollectionUtils.isEmpty(backupObjectList)) {
            throw new Exception("backupObjectList is empty.");
        }

        backupObjectList.forEach(this::sendToNextStep);

        throw new Exception("Backup Executor is not returned messages.");
    }

    @Override public IlluminatiTemplateInterfaceModelImpl deQueueByDebug () throws Exception {
        ILLUMINATI_EXECUTOR_LOGGER.info("ILLUMINATI_BLOCKING_QUEUE current size is {}", this.getQueueSize());

        if (illuminatiBlockingQueue == null || this.getQueueSize() == 0) {
            throw new Exception("backupObjectList is empty.");
        }

        final long start = System.currentTimeMillis();
        this.deQueue();
        final long elapsedTime = System.currentTimeMillis() - start;
        ILLUMINATI_EXECUTOR_LOGGER.info("ILLUMINATI_BLOCKING_QUEUE after inserted size is {}", this.getQueueSize());
        ILLUMINATI_EXECUTOR_LOGGER.info("elapsed time of dequeueing ILLUMINATI_BLOCKING_QUEUE is {}  millisecond", elapsedTime);

        throw new Exception("Backup Executor is not returned messages.");
    }

    @Override public void sendToNextStep(IlluminatiTemplateInterfaceModelImpl illuminatiTemplateInterfaceModel) {
        if (illuminatiTemplateInterfaceModel == null) {
            ILLUMINATI_EXECUTOR_LOGGER.warn("data is not valid");
            return;
        }
        if (this.DBExecutor == null) {
            ILLUMINATI_EXECUTOR_LOGGER.warn("ILLUMINATI_BACKUP Object is null");
            return;
        }
        //## Save file
        this.DBExecutor.appendByJsonString(IlluminatiInterfaceType.TEMPLATE_EXECUTOR, IlluminatiConstant.ILLUMINATI_GSON_OBJ.toJson(illuminatiTemplateInterfaceModel));
    }

    @Override protected void sendToNextStepByDebug(IlluminatiTemplateInterfaceModelImpl illuminatiBackupInterfaceModel) {
        final long start = System.currentTimeMillis();
        //## Save file
        this.sendToNextStep(illuminatiBackupInterfaceModel);
        final long elapsedTime = System.currentTimeMillis() - start;
        ILLUMINATI_EXECUTOR_LOGGER.info("elapsed time of template queue sent is {} millisecond", elapsedTime);
    }

    @Override protected void createSystemThread () {
        final Runnable runnableFirst = () -> {
            while (!IlluminatiGracefulShutdownChecker.getIlluminatiReadyToShutdown()) {
                try {
                    if (!IlluminatiConstant.ILLUMINATI_DEBUG) {
                        deQueue();
                    } else {
                        deQueueByDebug();
                    }
                } catch (Exception e) {
                    ILLUMINATI_EXECUTOR_LOGGER.debug("Failed to send the ILLUMINATI_BLOCKING_QUEUE... ({})", e.getMessage());
                } finally {
                    try {
                        Thread.sleep(BACKUP_THREAD_SLEEP_TIME);
                    } catch (InterruptedException ignore) {}
                }
            }
        };

        SystemUtil.createSystemThread(runnableFirst, this.getClass().getName() + " : ILLUMINATI_SAVE_DATA_TO_FILE_THREAD");

        // if you set debug is true
        this.createDebugThread();
    }

    @Override protected void preventErrorOfSystemThread(IlluminatiTemplateInterfaceModelImpl illuminatiTemplateInterfaceModel) {

    }

    public void createStopThread() {
        final Runnable runnableFirst = () -> {
            while (getQueueSize() > 0) {
                try {
                    deQueue();
                } catch (Exception ignore) {}
            }
        };

        SystemUtil.createSystemThread(runnableFirst, this.getClass().getName() + " : ILLUMINATI_STOP_THREAD");

    }
}
