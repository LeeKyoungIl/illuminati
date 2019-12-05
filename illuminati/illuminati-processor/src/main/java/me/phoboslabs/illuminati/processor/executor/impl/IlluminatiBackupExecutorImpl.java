package me.phoboslabs.illuminati.processor.executor.impl;

import me.phoboslabs.illuminati.processor.executor.IlluminatiBasicExecutor;
import me.phoboslabs.illuminati.processor.executor.IlluminatiBlockingQueue;
import me.phoboslabs.illuminati.processor.infra.backup.Backup;
import me.phoboslabs.illuminati.processor.infra.backup.BackupFactory;
import me.phoboslabs.illuminati.processor.infra.backup.shutdown.IlluminatiGracefulShutdownChecker;
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

    private final Backup<IlluminatiInterfaceModel> backup;

    private IlluminatiBackupExecutorImpl() throws Exception {
        super(ILLUMINATI_FILE_BACKUP_ENQUEUING_TIMEOUT_MS, new IlluminatiBlockingQueue<>(ILLUMINATI_BAK_LOG, POLL_PER_COUNT));
        this.backup = BackupFactory.getBackupInstance(IlluminatiConstant.ILLUMINATI_BACKUP_STORAGE_TYPE);
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

    @Override public void init() {
        if (this.backup == null) {
            return;
        }
        this.createSystemThread();
    }

    @Override public IlluminatiTemplateInterfaceModelImpl deQueue() throws Exception {
        List<IlluminatiTemplateInterfaceModelImpl> backupObjectList = illuminatiBlockingQueue.pollToList(ILLUMINATI_FILE_BACKUP_DEQUEUING_TIMEOUT_MS, TimeUnit.MILLISECONDS);

        if (CollectionUtils.isEmpty(backupObjectList)) {
            throw new Exception("backupObjectList is empty.");
        }

        for (IlluminatiTemplateInterfaceModelImpl illuminatiInterfaceModel : backupObjectList) {
            this.sendToNextStep(illuminatiInterfaceModel);
        }

        throw new Exception("Backup Executor is not returned messages.");
    }

    @Override public IlluminatiTemplateInterfaceModelImpl deQueueByDebug () throws Exception {
        illuminatiExecutorLogger.info("ILLUMINATI_BLOCKING_QUEUE current size is "+this.getQueueSize());

        if (illuminatiBlockingQueue == null || this.getQueueSize() == 0) {
            throw new Exception("backupObjectList is empty.");
        }

        final long start = System.currentTimeMillis();
        this.deQueue();
        final long elapsedTime = System.currentTimeMillis() - start;
        illuminatiExecutorLogger.info("ILLUMINATI_BLOCKING_QUEUE after inserted size is "+String.valueOf(this.getQueueSize()));
        illuminatiExecutorLogger.info("elapsed time of dequeueing ILLUMINATI_BLOCKING_QUEUE is "+elapsedTime+" millisecond");

        throw new Exception("Backup Executor is not returned messages.");
    }

    @Override public void sendToNextStep(IlluminatiTemplateInterfaceModelImpl illuminatiTemplateInterfaceModel) {
        if (illuminatiTemplateInterfaceModel == null) {
            illuminatiExecutorLogger.warn("data is not valid");
            return;
        }
        if (this.backup == null) {
            illuminatiExecutorLogger.warn("ILLUMINATI_BACKUP Object is null");
            return;
        }
        //## Save file
        this.backup.appendByJsonString(IlluminatiInterfaceType.TEMPLATE_EXECUTOR, IlluminatiConstant.ILLUMINATI_GSON_OBJ.toJson(illuminatiTemplateInterfaceModel));
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
                } catch (Exception e) {
                    illuminatiExecutorLogger.debug("Failed to send the ILLUMINATI_BLOCKING_QUEUE.. ("+e.getMessage()+")");
                } finally {
                    try {
                        Thread.sleep(BACKUP_THREAD_SLEEP_TIME);
                    } catch (InterruptedException ignore) {}
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
                } catch (Exception ignore) {}
            }
            }
        };

        SystemUtil.createSystemThread(runnableFirst, this.getClass().getName() + " : ILLUMINATI_STOP_THREAD");

    }
}
