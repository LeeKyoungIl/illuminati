package com.leekyoungil.illuminati.client.prossor.executor.impl;

import com.leekyoungil.illuminati.client.prossor.executor.IlluminatiBasicExecutor;
import com.leekyoungil.illuminati.client.prossor.executor.IlluminatiBlockingQueue;
import com.leekyoungil.illuminati.client.prossor.infra.backup.Backup;
import com.leekyoungil.illuminati.client.prossor.infra.backup.BackupFactory;
import com.leekyoungil.illuminati.client.prossor.infra.backup.shutdown.IlluminatiGracefulShutdownChecker;
import com.leekyoungil.illuminati.common.constant.IlluminatiConstant;
import com.leekyoungil.illuminati.common.dto.IlluminatiInterfaceModel;
import com.leekyoungil.illuminati.common.dto.enums.IlluminatiInterfaceType;
import com.leekyoungil.illuminati.common.dto.impl.IlluminatiTemplateInterfaceModelImpl;
import com.leekyoungil.illuminati.common.util.SystemUtil;
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
    private static final long ILLUMINATI_FILE_BACKUP_DEQUEUING_TIMEOUT_MS = 3000;
    private static final long ILLUMINATI_FILE_BACKUP_ENQUEUING_TIMEOUT_MS = 3000;

    // ################################################################################################################
    // ### init illuminati file backup queue                                                                        ###
    // ################################################################################################################
    private static final int POLL_PER_COUNT = 1000;
    private static final long BACKUP_THREAD_SLEEP_TIME = 300000l;

    private static final Backup<IlluminatiInterfaceModel> ILLUMINATI_BACKUP = BackupFactory.getBackupInstance(IlluminatiConstant.ILLUMINATI_BACKUP_STORAGE_TYPE);

    private IlluminatiBackupExecutorImpl() {
        super(ILLUMINATI_FILE_BACKUP_ENQUEUING_TIMEOUT_MS, new IlluminatiBlockingQueue<IlluminatiTemplateInterfaceModelImpl>(ILLUMINATI_BAK_LOG, POLL_PER_COUNT));
    }

    public static IlluminatiBackupExecutorImpl getInstance () {
        if (ILLUMINATI_BACKUP_EXECUTOR_IMPL == null) {
            synchronized (IlluminatiBackupExecutorImpl.class) {
                if (ILLUMINATI_BACKUP_EXECUTOR_IMPL == null) {
                    ILLUMINATI_BACKUP_EXECUTOR_IMPL = new IlluminatiBackupExecutorImpl();
                }
            }
        }

        return ILLUMINATI_BACKUP_EXECUTOR_IMPL;
    }

    @Override public void init() {
        if (ILLUMINATI_BACKUP != null) {
            this.createSystemThread();
        }
    }

    @Override public IlluminatiTemplateInterfaceModelImpl deQueue() {
        List<IlluminatiTemplateInterfaceModelImpl> backupObjectList = illuminatiBlockingQueue.pollToList(ILLUMINATI_FILE_BACKUP_DEQUEUING_TIMEOUT_MS, TimeUnit.MILLISECONDS);

        if (CollectionUtils.isEmpty(backupObjectList) == true) {
            return null;
        }

        for (IlluminatiTemplateInterfaceModelImpl illuminatiInterfaceModel : backupObjectList) {
            this.sendToNextStep(illuminatiInterfaceModel);
        }

        return null;
    }

    @Override public IlluminatiTemplateInterfaceModelImpl deQueueByDebug () {
        illuminatiExecutorLogger.info("ILLUMINATI_BLOCKING_QUEUE current size is "+String.valueOf(this.getQueueSize()));

        if (illuminatiBlockingQueue == null || this.getQueueSize() == 0) {
            return null;
        }

        final long start = System.currentTimeMillis();
        this.deQueue();
        final long elapsedTime = System.currentTimeMillis() - start;
        illuminatiExecutorLogger.info("ILLUMINATI_BLOCKING_QUEUE after inserted size is "+String.valueOf(this.getQueueSize()));
        illuminatiExecutorLogger.info("elapsed time of dequeueing ILLUMINATI_BLOCKING_QUEUE is "+elapsedTime+" millisecond");
        return null;
    }

    @Override public void sendToNextStep(IlluminatiTemplateInterfaceModelImpl illuminatiTemplateInterfaceModel) {
        if (illuminatiTemplateInterfaceModel == null) {
            illuminatiExecutorLogger.warn("data is not valid");
            return;
        }
        if (ILLUMINATI_BACKUP == null) {
            illuminatiExecutorLogger.warn("ILLUMINATI_BACKUP Object is null");
            return;
        }
        //## Save file
        ILLUMINATI_BACKUP.appendByJsonString(IlluminatiInterfaceType.TEMPLATE_EXECUTOR, IlluminatiConstant.ILLUMINATI_GSON_OBJ.toJson(illuminatiTemplateInterfaceModel));
    }

    @Override protected void sendToNextStepByDebug(IlluminatiTemplateInterfaceModelImpl illuminatiBackupInterfaceModel) {
        final long start = System.currentTimeMillis();
        //## Save file
        this.sendToNextStep(illuminatiBackupInterfaceModel);
        final long elapsedTime = System.currentTimeMillis() - start;
        illuminatiExecutorLogger.info("elapsed time of template queue sent is "+elapsedTime+" millisecond");
    }

    @Override protected void createSystemThread () {
        final Runnable runnableFirst = new Runnable() {
            public void run() {
                while (true && IlluminatiGracefulShutdownChecker.getIlluminatiReadyToShutdown() == false) {
                    try {
                        if (IlluminatiConstant.ILLUMINATI_DEBUG == false) {
                            deQueue();
                        } else {
                            deQueueByDebug();
                        }

                        try {
                            Thread.sleep(BACKUP_THREAD_SLEEP_TIME);
                        } catch (InterruptedException e) {
                            // ignore
                        }
                    } catch (Exception e) {
                        illuminatiExecutorLogger.warn("Failed to send the ILLUMINATI_BLOCKING_QUEUE.. ("+e.getMessage()+")");
                    }
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
        final Runnable runnableFirst = new Runnable() {
            public void run() {
                while (getQueueSize() > 0) {
                    try {
                        deQueue();
                    } catch (Exception e) {

                    }
                }
            }
        };

        SystemUtil.createSystemThread(runnableFirst, this.getClass().getName() + " : ILLUMINATI_STOP_THREAD");

    }
}
