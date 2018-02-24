package com.leekyoungil.illuminati.client.prossor.infra.restore.impl;

import com.leekyoungil.illuminati.client.prossor.executor.IlluminatiExecutor;
import com.leekyoungil.illuminati.client.prossor.infra.backup.Backup;
import com.leekyoungil.illuminati.client.prossor.infra.backup.impl.H2Backup;
import com.leekyoungil.illuminati.client.prossor.infra.restore.Restore;
import com.leekyoungil.illuminati.common.constant.IlluminatiConstant;
import com.leekyoungil.illuminati.common.dto.IlluminatiInterfaceModel;
import com.leekyoungil.illuminati.common.dto.impl.IlluminatiTemplateInterfaceModelImpl;
import com.leekyoungil.illuminati.common.util.SystemUtil;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class RestoreTemplateData implements Restore {

    protected final Logger RestoreTemplateDataLogger = LoggerFactory.getLogger(this.getClass());

    private static RestoreTemplateData RESTORE_TEMPLATE_DATA;

    private static final Backup<IlluminatiInterfaceModel> H2_BACKUP = H2Backup.getInstance();

    // ################################################################################################################
    // ### init illuminati template executor                                                                        ###
    // ################################################################################################################
    private IlluminatiExecutor<IlluminatiTemplateInterfaceModelImpl> illuminatiTemplateExecutor;

    private RestoreTemplateData (final IlluminatiExecutor illuminatiExecutor) {
        this.illuminatiTemplateExecutor = illuminatiExecutor;
    }

    public static RestoreTemplateData getInstance (final IlluminatiExecutor illuminatiExecutor) {
        if (RESTORE_TEMPLATE_DATA == null) {
            synchronized (RestoreTemplateData.class) {
                if (RESTORE_TEMPLATE_DATA == null) {
                    RESTORE_TEMPLATE_DATA = new RestoreTemplateData(illuminatiExecutor);
                }
            }
        }

        return RESTORE_TEMPLATE_DATA;
    }

    @Override public void init () {
        this.createSystemThread();
    }

    @Override public void restoreToQueue () {
        if ((10000 - this.illuminatiTemplateExecutor.getQueueSize()) <= (1000 * 1.5)) {
            return;
        }

        final List<IlluminatiInterfaceModel> backupObjectList = H2_BACKUP.getDataByList(false, true, 0, 1000);
        if (CollectionUtils.isNotEmpty(backupObjectList) == true) {
            for (IlluminatiInterfaceModel illuminatiInterfaceModel : backupObjectList) {
                this.illuminatiTemplateExecutor.addToQueue((IlluminatiTemplateInterfaceModelImpl) illuminatiInterfaceModel);
            }
        }
    }

    private void createSystemThread () {
        final Runnable runnableFirst = new Runnable() {
            public void run() {
                while (true) {
                    try {
                        if (IlluminatiConstant.ILLUMINATI_DEBUG == false) {
                            restoreToQueue();
                        } else {

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
                        RestoreTemplateDataLogger.warn("Failed to send the ILLUMINATI_BLOCKING_QUEUE.. ("+e.getMessage()+")");
                    }
                }
            }
        };

        SystemUtil.createSystemThread(runnableFirst, this.getClass().getName() + " : ILLUMINATI_RESTORE_DATA_TO_TEMPLATE_THREAD");
    }
}
