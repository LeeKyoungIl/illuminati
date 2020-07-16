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

package me.phoboslabs.illuminati.restore.impl;

import me.phoboslabs.illuminati.common.constant.IlluminatiConstant;
import me.phoboslabs.illuminati.common.dto.IlluminatiModel;
import me.phoboslabs.illuminati.common.dto.impl.IlluminatiBasicModel;
import me.phoboslabs.illuminati.common.util.SystemUtil;
import me.phoboslabs.illuminati.processor.executor.IlluminatiBasicExecutor;
import me.phoboslabs.illuminati.processor.executor.IlluminatiExecutor;
import me.phoboslabs.illuminati.processor.infra.backup.Backup;
import me.phoboslabs.illuminati.processor.infra.backup.impl.H2Backup;
import me.phoboslabs.illuminati.processor.infra.common.IlluminatiInfraConstant;
import me.phoboslabs.illuminati.processor.infra.restore.Restore;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class RestoreTemplateData implements Restore {

    protected final Logger restoreTemplateDataLogger = LoggerFactory.getLogger(this.getClass());

    private static RestoreTemplateData RESTORE_TEMPLATE_DATA;

    private static final int RESTORE_CHECK_QUEUE_SIZE = 1500;
    private static final int LIMIT_COUNT = 1000;

    private final Backup<IlluminatiModel> h2Backup;

    // ################################################################################################################
    // ### init illuminati template executor                                                                        ###
    // ################################################################################################################
    private IlluminatiExecutor<IlluminatiBasicModel> illuminatiTemplateExecutor;

    private RestoreTemplateData (final IlluminatiExecutor illuminatiExecutor) throws Exception {
        h2Backup = H2Backup.getInstance(IlluminatiBasicModel.class);
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

    @Override public RestoreTemplateData init () {
        this.createSystemThread();
        return this;
    }

    @Override public void restoreToQueue () {
        if (!this.readyToRestoreQueue()) {
            return;
        }

        try {
            final List<IlluminatiModel> backupObjectList = this.h2Backup.getDataByList(false, true, 0, LIMIT_COUNT);
            if (CollectionUtils.isNotEmpty(backupObjectList)) {
                backupObjectList.forEach(illuminatiInterfaceModel -> this.illuminatiTemplateExecutor.addToQueue((IlluminatiBasicModel) illuminatiInterfaceModel));
            }
        } catch (Exception ex) {
            this.restoreTemplateDataLogger.error("check H2 database configurations.", ex);
        }
    }

    private boolean readyToRestoreQueue() {
        if (!IlluminatiInfraConstant.IS_CAN_CONNECT_TO_REMOTE_BROKER.get()) {
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
        final Runnable runnableFirst = () -> {
            while (true) {
                try {
                    if (IlluminatiInfraConstant.IS_CAN_CONNECT_TO_REMOTE_BROKER.get()) {
                        if (!IlluminatiConstant.ILLUMINATI_DEBUG) {
                            restoreToQueue();
                        } else {
                            restoreToQueueByDebug();

                            try {
                                Thread.sleep(5000);
                            } catch (InterruptedException ignore) {}
                        }
                    }

                    try {
                        Thread.sleep(300000);
                    } catch (InterruptedException ignore) {}
                } catch (Exception e) {
                    restoreTemplateDataLogger.debug("Failed to send the ILLUMINATI_BLOCKING_QUEUE... ("+e.getMessage()+")");
                }
            }
        };

        SystemUtil.createSystemThread(runnableFirst, this.getClass().getName() + " : ILLUMINATI_RESTORE_DATA_TO_TEMPLATE_THREAD");
    }
}
