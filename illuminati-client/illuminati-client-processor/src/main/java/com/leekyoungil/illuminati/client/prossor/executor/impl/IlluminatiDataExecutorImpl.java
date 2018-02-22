package com.leekyoungil.illuminati.client.prossor.executor.impl;

import com.leekyoungil.illuminati.client.prossor.executor.IlluminatiBasicExecutor;
import com.leekyoungil.illuminati.client.prossor.executor.IlluminatiExecutor;
import com.leekyoungil.illuminati.client.prossor.properties.IlluminatiPropertiesImpl;
import com.leekyoungil.illuminati.common.dto.impl.IlluminatiDataInterfaceModelImpl;
import com.leekyoungil.illuminati.common.dto.impl.IlluminatiTemplateInterfaceModelImpl;
import com.leekyoungil.illuminati.common.dto.ServerInfo;
import com.leekyoungil.illuminati.common.properties.IlluminatiPropertiesHelper;
import com.leekyoungil.illuminati.common.util.SystemUtil;

import java.util.Map;

/**
 * Created by leekyoungil (leekyoungil@gmail.com) on 12/01/2017.
 */
public class IlluminatiDataExecutorImpl extends IlluminatiBasicExecutor<IlluminatiDataInterfaceModelImpl> {

    private static IlluminatiDataExecutorImpl ILLUMINATI_DATA_EXECUTOR_IMPL;

    // ################################################################################################################
    // ### init illuminati data queue                                                                               ###
    // ################################################################################################################
    private static final int ILLUMINATI_DATA_BAK_LOG = 10000;
    private static final long ILLUMINATI_DATA_DEQUEUING_TIMEOUT_MS = 1000l;
    private static final long ILLUMINATI_DATA_ENQUEUING_TIMEOUT_MS = 0l;

    // ################################################################################################################
    // ### init illuminati template executor                                                                        ###
    // ################################################################################################################
    private IlluminatiExecutor<IlluminatiTemplateInterfaceModelImpl> illuminatiTemplateExecutor;

    // ################################################################################################################
    // ### init illuminati basic system variables                                                                   ###
    // ################################################################################################################
    private final static String PARENT_MODULE_NAME = IlluminatiPropertiesHelper.getPropertiesValueByKey(IlluminatiPropertiesImpl.class, null, "illuminati", "parentModuleName", "no Name");
    private final static ServerInfo SERVER_INFO = new ServerInfo(true);
    // get basic JVM setting info only once.
    private final static Map<String, Object> JVM_INFO = SystemUtil.getJvmInfo();

    private IlluminatiDataExecutorImpl (final IlluminatiExecutor illuminatiExecutor) {
        super(ILLUMINATI_DATA_BAK_LOG, ILLUMINATI_DATA_ENQUEUING_TIMEOUT_MS, ILLUMINATI_DATA_DEQUEUING_TIMEOUT_MS, new IlluminatiBlockingQueue<IlluminatiDataInterfaceModelImpl>());
        this.illuminatiTemplateExecutor = illuminatiExecutor;
    }

    public static IlluminatiDataExecutorImpl getInstance (final IlluminatiExecutor illuminatiExecutor) {
        if (ILLUMINATI_DATA_EXECUTOR_IMPL == null) {
            synchronized (IlluminatiDataExecutorImpl.class) {
                if (ILLUMINATI_DATA_EXECUTOR_IMPL == null) {
                    ILLUMINATI_DATA_EXECUTOR_IMPL = new IlluminatiDataExecutorImpl(illuminatiExecutor);
                }
            }
        }

        return ILLUMINATI_DATA_EXECUTOR_IMPL;
    }

    @Override public synchronized void init () {
        // create illuminati template queue thread for send to the IlluminatiDataInterfaceModelImpl.
        this.createSystemThread();
    }

    // ################################################################################################################
    // ### public methods                                                                                           ###
    // ################################################################################################################

    @Override public void sendToNextStep(final IlluminatiDataInterfaceModelImpl illuminatiDataInterfaceModelImpl) {
        if (illuminatiDataInterfaceModelImpl.isValid() == false) {
            illuminatiExecutorLogger.warn("illuminatiDataInterfaceModelImpl is not valid");
            return;
        }
        //## send To Illuminati template queue
        this.sendToIlluminatiTemplateQueue(illuminatiDataInterfaceModelImpl);
    }

    // ################################################################################################################
    // ### private methods                                                                                          ###
    // ################################################################################################################

    private void addDataOnIlluminatiModel (final IlluminatiTemplateInterfaceModelImpl illuminatiTemplateInterfaceModelImpl) {
        illuminatiTemplateInterfaceModelImpl.initStaticInfo(PARENT_MODULE_NAME, SERVER_INFO);
        illuminatiTemplateInterfaceModelImpl.initBasicJvmInfo(JVM_INFO);
        illuminatiTemplateInterfaceModelImpl.addBasicJvmMemoryInfo(SystemUtil.getJvmMemoryInfo());
        illuminatiTemplateInterfaceModelImpl.setJavascriptUserAction();
    }

    private void sendToIlluminatiTemplateQueue (final IlluminatiDataInterfaceModelImpl illuminatiDataInterfaceModelImpl) {
        try {
            final IlluminatiTemplateInterfaceModelImpl illuminatiTemplateInterfaceModelImpl = new IlluminatiTemplateInterfaceModelImpl(illuminatiDataInterfaceModelImpl);
            this.addDataOnIlluminatiModel(illuminatiTemplateInterfaceModelImpl);
            this.illuminatiTemplateExecutor.addToQueue(illuminatiTemplateInterfaceModelImpl);
        } catch (Exception ex) {
            illuminatiExecutorLogger.debug("error : check your broker. ("+ex.toString()+")");
        }
    }

    @Override public void sendToNextStepByDebug (final IlluminatiDataInterfaceModelImpl illuminatiDataInterfaceModelImpl) {
        final long start = System.currentTimeMillis();
        this.sendToNextStep(illuminatiDataInterfaceModelImpl);
        final long elapsedTime = System.currentTimeMillis() - start;
        illuminatiExecutorLogger.info("data queue current size is "+String.valueOf(this.getQueueSize()));
        illuminatiExecutorLogger.info("elapsed time of template queue sent is "+elapsedTime+" millisecond");
    }

    @Override protected void preventErrorOfSystemThread(IlluminatiDataInterfaceModelImpl illuminatiDataInterfaceModel) {

    }
}
