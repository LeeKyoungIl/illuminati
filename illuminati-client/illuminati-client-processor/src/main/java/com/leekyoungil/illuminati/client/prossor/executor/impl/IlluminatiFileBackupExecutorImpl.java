package com.leekyoungil.illuminati.client.prossor.executor.impl;

import com.leekyoungil.illuminati.client.prossor.executor.IlluminatiBasicExecutor;
import com.leekyoungil.illuminati.client.prossor.executor.IlluminatiExecutor;
import com.leekyoungil.illuminati.client.prossor.infra.backup.Backup;
import com.leekyoungil.illuminati.client.prossor.infra.backup.impl.H2Backup;
import com.leekyoungil.illuminati.client.prossor.properties.IlluminatiPropertiesImpl;
import com.leekyoungil.illuminati.common.constant.IlluminatiConstant;
import com.leekyoungil.illuminati.common.dto.IlluminatiInterfaceModel;
import com.leekyoungil.illuminati.common.dto.enums.IlluminatiInterfaceType;
import com.leekyoungil.illuminati.common.dto.impl.IlluminatiDataInterfaceModelImpl;
import com.leekyoungil.illuminati.common.dto.impl.IlluminatiFileBackupInterfaceModelImpl;
import com.leekyoungil.illuminati.common.dto.impl.IlluminatiTemplateInterfaceModelImpl;
import com.leekyoungil.illuminati.common.properties.IlluminatiPropertiesHelper;
import com.leekyoungil.illuminati.common.util.SystemUtil;
import org.apache.commons.collections.CollectionUtils;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class IlluminatiFileBackupExecutorImpl extends IlluminatiBasicExecutor<IlluminatiInterfaceModel> {

    private static IlluminatiFileBackupExecutorImpl ILLUMINATI_FILE_BACKUP_EXECUTOR_IMPL;

    // ################################################################################################################
    // ### init illuminati file backup queue                                                                        ###
    // ################################################################################################################
    private static final int ILLUMINATI_FILE_BACKUP_LOG = 100000;
    private static final int ILLUMINATI_RESTORE_FILE_BACKUP_LOG = 10000;
    private static final long ILLUMINATI_FILE_BACKUP_DEQUEUING_TIMEOUT_MS = 3000;
    private static final long ILLUMINATI_FILE_BACKUP_ENQUEUING_TIMEOUT_MS = 3000;

    // ################################################################################################################
    // ### init illuminati file backup queue                                                                        ###
    // ################################################################################################################
    private static final int RESTORE_PER_COUNT = 1000;
    private static final long RESTORE_SLEEP_TIME = 600000;

    // ################################################################################################################
    // ### init illuminati template executor                                                                        ###
    // ################################################################################################################
    private IlluminatiExecutor<IlluminatiTemplateInterfaceModelImpl> illuminatiTemplateExecutor = IlluminatiTemplateExecutorImpl.getInstance();

    private static final Backup<IlluminatiInterfaceModel> H2_BACKUP = H2Backup.getInstance();

    // ################################################################################################################
    // ### init illuminati file base path                                                                           ###
    // ################################################################################################################
    private static final String BASE_PATH;
    private static final String DEFAULT_BASE_PATH = "./log";

    static {
        BASE_PATH = IlluminatiPropertiesHelper.getPropertiesValueByKey(IlluminatiPropertiesImpl.class, null, "illuminati", "baseFilePath", DEFAULT_BASE_PATH);
    }

    private IlluminatiFileBackupExecutorImpl () {
        super(ILLUMINATI_FILE_BACKUP_LOG, ILLUMINATI_FILE_BACKUP_ENQUEUING_TIMEOUT_MS, ILLUMINATI_FILE_BACKUP_DEQUEUING_TIMEOUT_MS, new IlluminatiBlockingQueue<IlluminatiInterfaceModel>(ILLUMINATI_RESTORE_FILE_BACKUP_LOG));
    }

    public static IlluminatiFileBackupExecutorImpl getInstance () {
        if (ILLUMINATI_FILE_BACKUP_EXECUTOR_IMPL == null) {
            synchronized (IlluminatiFileBackupExecutorImpl.class) {
                if (ILLUMINATI_FILE_BACKUP_EXECUTOR_IMPL == null) {
                    ILLUMINATI_FILE_BACKUP_EXECUTOR_IMPL = new IlluminatiFileBackupExecutorImpl();
                }
            }
        }

        return ILLUMINATI_FILE_BACKUP_EXECUTOR_IMPL;
    }

    @Override public void init() {
        this.createSystemThread();
    }

    @Override public IlluminatiInterfaceModel deQueue() {
        List<IlluminatiInterfaceModel> backupObjectList = illuminatiBlockingQueue.pollToList(ILLUMINATI_FILE_BACKUP_DEQUEUING_TIMEOUT_MS, TimeUnit.MILLISECONDS);

        if (CollectionUtils.isEmpty(backupObjectList) == true) {
            return null;
        }

        for (IlluminatiInterfaceModel illuminatiInterfaceModel : backupObjectList) {
            this.sendToNextStep(illuminatiInterfaceModel);
        }

        return null;
    }

    @Override public IlluminatiInterfaceModel deQueueByDebug () {
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

    @Override public void sendToNextStep(IlluminatiInterfaceModel illuminatiFileBackupInterfaceModel) {
        if (illuminatiFileBackupInterfaceModel == null) {
            illuminatiExecutorLogger.warn("data is not valid");
            return;
        }
        //## Save file
        H2_BACKUP.append(IlluminatiInterfaceType.TEMPLATE_EXECUTOR, illuminatiFileBackupInterfaceModel);
    }

    @Override protected void sendToNextStepByDebug(IlluminatiInterfaceModel illuminatiFileBackupInterfaceModel) {
        final long start = System.currentTimeMillis();
        //## Save file
        this.sendToNextStep(illuminatiFileBackupInterfaceModel);
        final long elapsedTime = System.currentTimeMillis() - start;
        illuminatiExecutorLogger.info("elapsed time of template queue sent is "+elapsedTime+" millisecond");
    }

    @Override protected void createSystemThread () {
        final Runnable runnableFirst = new Runnable() {
            public void run() {
                while (true) {
                    try {
                        if (IlluminatiConstant.ILLUMINATI_DEBUG == false) {
                            deQueue();
                        } else {
                            deQueueByDebug();

                            try {
                                Thread.sleep(5000);
                            } catch (InterruptedException e) {
                                // ignore
                            }
                        }

                        try {
                            Thread.sleep(30000);
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

    private void restoreToTemplateQueueSystemThread () {
        final Runnable runnableFirst = new Runnable() {
            final IlluminatiExecutor illuminatiExecutor = IlluminatiTemplateExecutorImpl.getInstance();

            public void run() {
                while (true) {
                    try {
                        if (IlluminatiConstant.ILLUMINATI_DEBUG == false) {
                            if ((10000 - illuminatiExecutor.getQueueSize()) > 2000) {
                                final List<IlluminatiInterfaceModel> backupObjectList = H2_BACKUP.getDataByList(false, true, 0, 1000);
                                if (CollectionUtils.isNotEmpty(backupObjectList) == true) {
                                    for (IlluminatiInterfaceModel illuminatiInterfaceModel : backupObjectList) {
                                        illuminatiExecutor.addToQueue(illuminatiInterfaceModel);
                                    }
                                }
                            }
                        }

                        try {
                            Thread.sleep(RESTORE_SLEEP_TIME);
                        } catch (InterruptedException e) {
                            // ignore
                        }
                    } catch (Exception e) {
                        illuminatiExecutorLogger.warn("Failed to send the ILLUMINATI_BLOCKING_QUEUE.. ("+e.getMessage()+")");
                    }
                }
            }
        };

        SystemUtil.createSystemThread(runnableFirst, this.getClass().getName() + " : ILLUMINATI_RESTORE_DATA_TO_FILE_THREAD");
    }

    @Override protected void preventErrorOfSystemThread(IlluminatiInterfaceModel illuminatiInterfaceModel) {

    }
}
