package com.leekyoungil.illuminati.client.prossor.executor.impl;

import com.leekyoungil.illuminati.client.prossor.executor.IlluminatiBasicExecutor;
import com.leekyoungil.illuminati.client.prossor.properties.IlluminatiPropertiesImpl;
import com.leekyoungil.illuminati.common.constant.IlluminatiConstant;
import com.leekyoungil.illuminati.common.properties.IlluminatiPropertiesHelper;
import com.leekyoungil.illuminati.common.util.FileUtil;
import com.leekyoungil.illuminati.common.util.StringObjectUtils;
import com.leekyoungil.illuminati.common.util.SystemUtil;

import java.io.File;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class IlluminatiFileBackupExecutorImpl extends IlluminatiBasicExecutor<String> {

    private static IlluminatiFileBackupExecutorImpl ILLUMINATI_FILE_BACKUP_EXECUTOR_IMPL;

    // ################################################################################################################
    // ### init illuminati file backup queue                                                                        ###
    // ################################################################################################################
    private static final int ILLUMINATI_FILE_BACKUP_LOG = 10000;
    private static final long ILLUMINATI_FILE_BACKUP_DEQUEUING_TIMEOUT_MS = 3000;
    private static final long ILLUMINATI_FILE_BACKUP_ENQUEUING_TIMEOUT_MS = 3000;

    // ################################################################################################################
    // ### init illuminati file base path                                                                           ###
    // ################################################################################################################
    private static final String BASE_PATH;
    private static final String DEFAULT_BASE_PATH = "./log";

    static {
        BASE_PATH = IlluminatiPropertiesHelper.getPropertiesValueByKey(IlluminatiPropertiesImpl.class, null, "illuminati", "baseFilePath", DEFAULT_BASE_PATH);
    }

    private IlluminatiFileBackupExecutorImpl () {
        super(ILLUMINATI_FILE_BACKUP_LOG, ILLUMINATI_FILE_BACKUP_ENQUEUING_TIMEOUT_MS, ILLUMINATI_FILE_BACKUP_DEQUEUING_TIMEOUT_MS, new IlluminatiBlockingQueue<String>(10));
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

    @Override public String deQueue() {
        if (IlluminatiConstant.ILLUMINATI_DEBUG == false) {
            List<String> fileTextDataList = illuminatiBlockingQueue.pollToList(ILLUMINATI_FILE_BACKUP_DEQUEUING_TIMEOUT_MS, TimeUnit.MILLISECONDS);
            StringBuilder fileTextString = new StringBuilder();

            for (String textData : fileTextDataList) {
                fileTextString.append(textData);
                fileTextString.append("\r\n");
            }

            return fileTextString.toString();
        } else {
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                // ignore
            }

            return this.deQueueByDebug();
        }
    }

    @Override public String deQueueByDebug () {
        illuminatiExecutorLogger.info("ILLUMINATI_BLOCKING_QUEUE current size is "+String.valueOf(this.getQueueSize()));

        if (illuminatiBlockingQueue == null || this.getQueueSize() == 0) {
            return null;
        }

        final long start = System.currentTimeMillis();
        List<String> fileTextDataList = illuminatiBlockingQueue.pollToList(ILLUMINATI_FILE_BACKUP_DEQUEUING_TIMEOUT_MS, TimeUnit.MILLISECONDS);
        StringBuilder fileTextString = new StringBuilder();

        for (String textData : fileTextDataList) {
            fileTextString.append(textData);
            fileTextString.append("\r\n");
        }
        final long elapsedTime = System.currentTimeMillis() - start;
        illuminatiExecutorLogger.info("ILLUMINATI_BLOCKING_QUEUE after inserted size is "+String.valueOf(this.getQueueSize()));
        illuminatiExecutorLogger.info("elapsed time of dequeueing ILLUMINATI_BLOCKING_QUEUE is "+elapsedTime+" millisecond");
        return fileTextString.toString();
    }

    @Override public void sendToNextStep(String textData) {
        //## Save file
        if (FileUtil.createDirectory(BASE_PATH) == true) {
            File file = FileUtil.generateFile(BASE_PATH, FileUtil.generateFileName());
            FileUtil.appendDataToFileByOnce(file, textData);
        }
    }

    @Override protected void sendToNextStepByDebug(String textData) {
        if (StringObjectUtils.isValid(textData) == false) {
            illuminatiExecutorLogger.warn("textData is not valid");
            return;
        }

        final long start = System.currentTimeMillis();
        //## Save file
        if (FileUtil.createDirectory(BASE_PATH) == true) {
            File file = FileUtil.generateFile(BASE_PATH, FileUtil.generateFileName());
            FileUtil.appendDataToFileByOnce(file, textData);
        }
        final long elapsedTime = System.currentTimeMillis() - start;
        illuminatiExecutorLogger.info("elapsed time of template queue sent is "+elapsedTime+" millisecond");
    }

    @Override protected void createSystemThread () {
        final Runnable runnableFirst = new Runnable() {
            public void run() {
                while (true) {
                    try {
                        final String textData = deQueue();
                        if (StringObjectUtils.isValid(textData) == true) {
                            if (IlluminatiConstant.ILLUMINATI_DEBUG == false) {
                                sendToNextStep(textData);
                            } else {
                                try {
                                    Thread.sleep(5000);
                                } catch (InterruptedException e) {
                                    // ignore
                                }
                                sendToNextStepByDebug(textData);
                            }
                        }
                    } catch (Exception e) {
                        illuminatiExecutorLogger.warn("Failed to send the ILLUMINATI_BLOCKING_QUEUE.. ("+e.getMessage()+")");
                    }
                }
            }
        };

        SystemUtil.createSystemThread(runnableFirst, this.getClass().getName() + " : ILLUMINATI_SENDER_THREAD");

        // if you set debug is true
        this.createDebugThread();
    }
}
