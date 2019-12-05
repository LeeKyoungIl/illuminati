package me.phoboslabs.illuminati.processor.infra.restore.impl;

import me.phoboslabs.illuminati.processor.executor.IlluminatiExecutor;
import me.phoboslabs.illuminati.processor.infra.backup.Backup;
import me.phoboslabs.illuminati.processor.infra.backup.impl.H2Backup;
import me.phoboslabs.illuminati.processor.infra.common.IlluminatiInfraConstant;
import me.phoboslabs.illuminati.processor.infra.restore.Restore;
import me.phoboslabs.illuminati.common.constant.IlluminatiConstant;
import me.phoboslabs.illuminati.common.dto.IlluminatiInterfaceModel;
import me.phoboslabs.illuminati.common.dto.impl.IlluminatiTemplateInterfaceModelImpl;
import me.phoboslabs.illuminati.common.util.SystemUtil;
import me.phoboslabs.illuminati.processor.executor.IlluminatiBasicExecutor;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class RestoreTemplateData implements Restore {

    protected final Logger restoreTemplateDataLogger = LoggerFactory.getLogger(this.getClass());

    private static RestoreTemplateData RESTORE_TEMPLATE_DATA;

    private static final int RESTORE_CHECK_QUEUE_SIZE = 1500;
    private static final int LIMIT_COUNT = 1000;

    private final Backup<IlluminatiInterfaceModel> h2Backup;

    // ################################################################################################################
    // ### init illuminati template executor                                                                        ###
    // ################################################################################################################
    private IlluminatiExecutor<IlluminatiTemplateInterfaceModelImpl> illuminatiTemplateExecutor;

    private RestoreTemplateData (final IlluminatiExecutor illuminatiExecutor) throws Exception {
        h2Backup = H2Backup.getInstance(IlluminatiTemplateInterfaceModelImpl.class);
        this.illuminatiTemplateExecutor = illuminatiExecutor;
    }

    public static RestoreTemplateData getInstance (final IlluminatiExecutor illuminatiExecutor) throws Exception {
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
        if (this.readyToRestoreQueue() == false) {
            return;
        }

        try {
            final List<IlluminatiInterfaceModel> backupObjectList = this.h2Backup.getDataByList(false, true, 0, LIMIT_COUNT);
            if (CollectionUtils.isNotEmpty(backupObjectList)) {
                for (IlluminatiInterfaceModel illuminatiInterfaceModel : backupObjectList) {
                    this.illuminatiTemplateExecutor.addToQueue((IlluminatiTemplateInterfaceModelImpl) illuminatiInterfaceModel);
                }
            }
        } catch (Exception ex) {
            this.restoreTemplateDataLogger.error("check H2 database configurations.", ex);
        }
    }

    private boolean readyToRestoreQueue() {
        if (IlluminatiInfraConstant.IS_CANCONNECT_TO_REMOTE_BROKER.get() == false) {
            return false;
        }

        try {
            if (this.h2Backup.getCount() == 0) {
                return false;
            }
        } catch (Exception ex) {
            return false;
        }

        final int restoreQueueSize = IlluminatiBasicExecutor.ILLUMINATI_BAK_LOG - this.illuminatiTemplateExecutor.getQueueSize();
        return restoreQueueSize > RESTORE_CHECK_QUEUE_SIZE;
    }

    @Override public void restoreToQueueByDebug () {
        final long start = System.currentTimeMillis();
        //## Restore file
        this.restoreToQueue();
        final long elapsedTime = System.currentTimeMillis() - start;
        this.restoreTemplateDataLogger.info("elapsed time of template queue sent is "+elapsedTime+" millisecond");
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
                    restoreTemplateDataLogger.debug("Failed to send the ILLUMINATI_BLOCKING_QUEUE.. ("+e.getMessage()+")");
                }
            }
            }
        };

        SystemUtil.createSystemThread(runnableFirst, this.getClass().getName() + " : ILLUMINATI_RESTORE_DATA_TO_TEMPLATE_THREAD");
    }
}
