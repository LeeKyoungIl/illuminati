package com.leekyoungil.illuminati.client.prossor.executor.impl;

import com.leekyoungil.illuminati.client.prossor.executor.IlluminatiBasicExecutor;
import com.leekyoungil.illuminati.client.prossor.properties.IlluminatiPropertiesImpl;
import com.leekyoungil.illuminati.common.dto.IlluminatiFileBackupInterfaceModel;
import com.leekyoungil.illuminati.common.properties.IlluminatiPropertiesHelper;
import com.leekyoungil.illuminati.common.util.FileUtil;

public class IlluminatiFileBackupExecutorImpl extends IlluminatiBasicExecutor<IlluminatiFileBackupInterfaceModel> {

    private static IlluminatiFileBackupExecutorImpl ILLUMINATI_FILE_BACKUP_EXECUTOR_IMPL;

    // ################################################################################################################
    // ### init illuminati file backup queue                                                                        ###
    // ################################################################################################################
    private static final int ILLUMINATI_FILE_BAKUP_LOG = 10000;
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
        super(ILLUMINATI_FILE_BAKUP_LOG, ILLUMINATI_FILE_BACKUP_ENQUEUING_TIMEOUT_MS, ILLUMINATI_FILE_BACKUP_DEQUEUING_TIMEOUT_MS);
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

    @Override public void sendToNextStep(IlluminatiFileBackupInterfaceModel illuminatiFileBackupInterfaceModel) {

    }

    @Override protected void sendToNextStepByDebug(IlluminatiFileBackupInterfaceModel illuminatiFileBackupInterfaceModel) {

    }

}
