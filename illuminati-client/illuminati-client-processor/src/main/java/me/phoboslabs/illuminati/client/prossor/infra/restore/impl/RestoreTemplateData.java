package me.phoboslabs.illuminati.client.prossor.infra.restore.impl;

import me.phoboslabs.illuminati.client.prossor.executor.IlluminatiExecutor;
import me.phoboslabs.illuminati.client.prossor.infra.backup.Backup;
import me.phoboslabs.illuminati.client.prossor.infra.backup.impl.H2Backup;
import me.phoboslabs.illuminati.client.prossor.infra.common.IlluminatiInfraConstant;
import me.phoboslabs.illuminati.client.prossor.infra.restore.Restore;
import me.phoboslabs.illuminati.client.prossor.executor.IlluminatiBasicExecutor;
import me.phoboslabs.illuminati.common.constant.IlluminatiConstant;
import me.phoboslabs.illuminati.common.dto.IlluminatiInterfaceModel;
import me.phoboslabs.illuminati.common.dto.impl.IlluminatiTemplateInterfaceModelImpl;
import me.phoboslabs.illuminati.common.util.SystemUtil;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class RestoreTemplateData implements Restore {

    protected final Logger RestoreTemplateDataLogger = LoggerFactory.getLogger(this.getClass());

    private static RestoreTemplateData RESTORE_TEMPLATE_DATA;

    private static final int RESTORE_CHECK_QUEUE_SIZE = 1500;
    private static final int LIMIT_COUNT = 1000;

    private static final Backup<IlluminatiInterfaceModel> H2_BACKUP = H2Backup.getInstance(IlluminatiTemplateInterfaceModelImpl.class);

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
        if (((IlluminatiBasicExecutor.ILLUMINATI_BAK_LOG - this.illuminatiTemplateExecutor.getQueueSize()) <= RESTORE_CHECK_QUEUE_SIZE)
                || H2_BACKUP.getCount() == 0 || IlluminatiInfraConstant.IS_CANCONNECT_TO_REMOTE_BROKER.get() == false) {
            return;
        }

        final List<IlluminatiInterfaceModel> backupObjectList = H2_BACKUP.getDataByList(false, true, 0, LIMIT_COUNT);
        if (CollectionUtils.isNotEmpty(backupObjectList)) {
            for (IlluminatiInterfaceModel illuminatiInterfaceModel : backupObjectList) {
                this.illuminatiTemplateExecutor.addToQueue((IlluminatiTemplateInterfaceModelImpl) illuminatiInterfaceModel);
            }
        }
    }

    @Override public void restoreToQueueByDebug () {
        final long start = System.currentTimeMillis();
        //## Restore file
        this.restoreToQueue();
        final long elapsedTime = System.currentTimeMillis() - start;
        RestoreTemplateDataLogger.info("elapsed time of template queue sent is "+elapsedTime+" millisecond");
    }

    private void createSystemThread () {
        final Runnable runnableFirst = new Runnable() {
            public void run() {
                while (true) {
                    try {
                        if (IlluminatiInfraConstant.IS_CANCONNECT_TO_REMOTE_BROKER.get()) {
                            if (IlluminatiConstant.ILLUMINATI_DEBUG == false) {
                                restoreToQueue();
                            } else {
                                restoreToQueueByDebug();

                                try {
                                    Thread.sleep(5000);
                                } catch (InterruptedException e) {
                                    // ignore
                                }
                            }
                        }

                        try {
                            Thread.sleep(300000);
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
