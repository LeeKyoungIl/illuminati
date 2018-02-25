package com.leekyoungil.illuminati.client.prossor.executor.impl;

import com.leekyoungil.illuminati.client.prossor.executor.IlluminatiBasicExecutor;
import com.leekyoungil.illuminati.client.prossor.executor.IlluminatiBlockingQueue;
import com.leekyoungil.illuminati.client.prossor.infra.backup.Backup;
import com.leekyoungil.illuminati.client.prossor.infra.backup.impl.H2Backup;
import com.leekyoungil.illuminati.client.prossor.properties.IlluminatiPropertiesImpl;
import com.leekyoungil.illuminati.common.constant.IlluminatiConstant;
import com.leekyoungil.illuminati.common.dto.IlluminatiInterfaceModel;
import com.leekyoungil.illuminati.common.dto.enums.IlluminatiInterfaceType;
import com.leekyoungil.illuminati.common.dto.impl.IlluminatiTemplateInterfaceModelImpl;
import com.leekyoungil.illuminati.common.properties.IlluminatiPropertiesHelper;
import com.leekyoungil.illuminati.common.util.SystemUtil;
import org.apache.commons.collections.CollectionUtils;

import java.util.List;
import java.util.concurrent.TimeUnit;

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

    private static final Backup<IlluminatiInterfaceModel> H2_BACKUP = H2Backup.getInstance();

    // ################################################################################################################
    // ### init illuminati file base path                                                                           ###
    // ################################################################################################################
    private static final String BASE_PATH;
    private static final String DEFAULT_BASE_PATH = "./log";

    static {
        BASE_PATH = IlluminatiPropertiesHelper.getPropertiesValueByKey(IlluminatiPropertiesImpl.class, null, "illuminati", "baseFilePath", DEFAULT_BASE_PATH);
    }

    private IlluminatiBackupExecutorImpl() {
        super(ILLUMINATI_FILE_BACKUP_ENQUEUING_TIMEOUT_MS, ILLUMINATI_FILE_BACKUP_DEQUEUING_TIMEOUT_MS, new IlluminatiBlockingQueue<IlluminatiTemplateInterfaceModelImpl>(ILLUMINATI_BAK_LOG, POLL_PER_COUNT));
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
        this.createSystemThread();
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
        //## Save file
        H2_BACKUP.append(IlluminatiInterfaceType.TEMPLATE_EXECUTOR, illuminatiTemplateInterfaceModel);
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

//    private void restoreToTemplateQueueSystemThread () {
//        final Runnable runnableFirst = new Runnable() {
//            public void run() {
//                while (true) {
//                    try {
//                        if (IlluminatiConstant.ILLUMINATI_DEBUG == false) {
//                            if ((10000 - illuminatiTemplateExecutor.getQueueSize()) > 2000) {
//                                final List<IlluminatiInterfaceModel> backupObjectList = H2_BACKUP.getDataByList(false, true, 0, 1000);
//                                if (CollectionUtils.isNotEmpty(backupObjectList) == true) {
//                                    for (IlluminatiInterfaceModel illuminatiInterfaceModel : backupObjectList) {
//                                        illuminatiTemplateExecutor.addToQueue((IlluminatiTemplateInterfaceModelImpl) illuminatiInterfaceModel);
//                                    }
//                                }
//                            }
//                        }
//
//                        try {
//                            Thread.sleep(RESTORE_SLEEP_TIME);
//                        } catch (InterruptedException e) {
//                            // ignore
//                        }
//                    } catch (Exception e) {
//                        illuminatiExecutorLogger.warn("Failed to send the ILLUMINATI_BLOCKING_QUEUE.. ("+e.getMessage()+")");
//                    }
//                }
//            }
//        };
//
//        SystemUtil.createSystemThread(runnableFirst, this.getClass().getName() + " : ILLUMINATI_RESTORE_DATA_TO_FILE_THREAD");
//    }

    @Override
    protected void preventErrorOfSystemThread(IlluminatiTemplateInterfaceModelImpl illuminatiTemplateInterfaceModel) {

    }
}
