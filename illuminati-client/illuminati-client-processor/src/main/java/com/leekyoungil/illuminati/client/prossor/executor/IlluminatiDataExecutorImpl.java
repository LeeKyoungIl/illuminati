package com.leekyoungil.illuminati.client.prossor.executor;

import com.leekyoungil.illuminati.client.prossor.properties.IlluminatiPropertiesImpl;
import com.leekyoungil.illuminati.common.IlluminatiCommon;
import com.leekyoungil.illuminati.common.constant.IlluminatiConstant;
import com.leekyoungil.illuminati.common.dto.IlluminatiDataInterfaceModel;
import com.leekyoungil.illuminati.common.dto.IlluminatiTemplateInterfaceModel;
import com.leekyoungil.illuminati.common.dto.RequestHeaderModel;
import com.leekyoungil.illuminati.common.dto.ServerInfo;
import com.leekyoungil.illuminati.common.dto.enums.IlluminatiTransactionIdType;
import com.leekyoungil.illuminati.common.properties.IlluminatiPropertiesHelper;
import com.leekyoungil.illuminati.common.util.ConvertUtil;
import com.leekyoungil.illuminati.common.util.SystemUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

public class IlluminatiDataExecutorImpl implements IlluminatiExecutor<IlluminatiDataInterfaceModel> {

    private static final Logger ILLUMINATI_DATA_EXECUTOR_LOGGER = LoggerFactory.getLogger(IlluminatiDataExecutorImpl.class);

    // ################################################################################################################
    // ### init illuminati data queue                                 z                                              ###
    // ################################################################################################################
    private static final int ILLUMINATI_DATA_BAK_LOG = 10000;
    private static final long ILLUMINATI_DATA_DEQUEUING_TIMEOUT_MS = 1000;
    private static final long ILLUMINATI_DATA_ENQUEUING_TIMEOUT_MS = 0;

    private final static BlockingQueue<IlluminatiDataInterfaceModel> ILLUMINATI_DATA_BLOCKING_QUEUE = new LinkedBlockingQueue<IlluminatiDataInterfaceModel>(ILLUMINATI_DATA_BAK_LOG);

    // ################################################################################################################
    // ### init illuminati template executor                                                                        ###
    // ################################################################################################################
    private static IlluminatiExecutor<IlluminatiTemplateInterfaceModel> ILLUMINATI_TEMPLATE_EXECUTOR = new IlluminatiTemplateExecutorImpl();

    // ################################################################################################################
    // ### init illuminati basic system variables                                                                   ###
    // ################################################################################################################
    private static String PARENT_MODULE_NAME;
    private static ServerInfo SERVER_INFO;
    private static Map<String, Object> JVM_INFO;

    @Override public synchronized void init () {
        IlluminatiCommon.init();

        ILLUMINATI_TEMPLATE_EXECUTOR.init();

        PARENT_MODULE_NAME = IlluminatiPropertiesHelper.getPropertiesValueByKey(IlluminatiPropertiesImpl.class, null, "illuminati", "parentModuleName");
        SERVER_INFO = new ServerInfo(true);
        // get basic JVM setting info only once.
        JVM_INFO = SystemUtil.getJvmInfo();

        // create illuminati template queue thread for send to the IlluminatiDataInterfaceModel.
        this.createSystemThread();
    }

    // ################################################################################################################
    // ### public methods                                                                                           ###
    // ################################################################################################################

    @Override public void addToQueue(final IlluminatiDataInterfaceModel illuminatiDataInterfaceModel) {
        try {
            ILLUMINATI_DATA_BLOCKING_QUEUE.offer(illuminatiDataInterfaceModel, ILLUMINATI_DATA_ENQUEUING_TIMEOUT_MS, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            ILLUMINATI_DATA_EXECUTOR_LOGGER.warn("Failed to enqueuing the Illuminati data queue.. ("+e.getMessage()+")");
        }
    }

    @Override public IlluminatiDataInterfaceModel deQueue() {
        if (ILLUMINATI_DATA_BLOCKING_QUEUE == null || this.getQueueSize() == 0) {
            return null;
        }
        try {
            return ILLUMINATI_DATA_BLOCKING_QUEUE.poll(ILLUMINATI_DATA_DEQUEUING_TIMEOUT_MS, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            ILLUMINATI_DATA_EXECUTOR_LOGGER.warn("Failed to dequeing the illuminati template queue.. ("+e.getMessage()+")");
        } catch (Exception e) {
            ILLUMINATI_DATA_EXECUTOR_LOGGER.warn("Failed to send the illuminati template queue.. ("+e.getMessage()+")");
        }

        return null;
    }

    @Override public void sendToNextStep(final IlluminatiDataInterfaceModel illuminatiDataInterfaceModel) {
        if (illuminatiDataInterfaceModel.isValid() == false) {
            return;
        }

        this.sendToIlluminatiTemplateQueue(illuminatiDataInterfaceModel);
    }

    @Override public int getQueueSize () {
        if (ILLUMINATI_DATA_BLOCKING_QUEUE == null) {
            ILLUMINATI_DATA_EXECUTOR_LOGGER.warn("ILLUMINATI_DATA_BLOCKING_QUEUE is must not null.");
            return 0;
        }
        return ILLUMINATI_DATA_BLOCKING_QUEUE.size();
    }

    @Override public void createSystemThread () {
        final Runnable runnableFirst = new Runnable() {
            public void run() {
                while (true) {
                    final IlluminatiDataInterfaceModel illuminatiDataInterfaceModel = deQueue();
                    if (illuminatiDataInterfaceModel != null) {
                        if (IlluminatiConstant.ILLUMINATI_DEBUG == false) {
                            sendToNextStep(illuminatiDataInterfaceModel);
                        } else {
                            //IlluminatiTemplateExecutorImpl.sendToIlluminatiByDebug(illuminatiTemplateInterfaceModel);
                        }
                    }
                }
            }
        };

        SystemUtil.createSystemThread(runnableFirst, "ILLUMINATI_SEND_TO_TEMPLATE_QUEUE_THREAD");
    }

    // ################################################################################################################
    // ### private methods                                                                                          ###
    // ################################################################################################################

    private void addDataOnIlluminatiModel (final IlluminatiTemplateInterfaceModel illuminatiTemplateInterfaceModel) {
        illuminatiTemplateInterfaceModel.initStaticInfo(PARENT_MODULE_NAME, SERVER_INFO);
        illuminatiTemplateInterfaceModel.initBasicJvmInfo(JVM_INFO);
        illuminatiTemplateInterfaceModel.addBasicJvmMemoryInfo(SystemUtil.getJvmMemoryInfo());
        illuminatiTemplateInterfaceModel.setJavascriptUserAction();
    }

    private void sendToIlluminatiTemplateQueue (final IlluminatiDataInterfaceModel illuminatiDataInterfaceModel) {
        try {
            final IlluminatiTemplateInterfaceModel illuminatiTemplateInterfaceModel = new IlluminatiTemplateInterfaceModel(illuminatiDataInterfaceModel);
            this.addDataOnIlluminatiModel(illuminatiTemplateInterfaceModel);
            ILLUMINATI_TEMPLATE_EXECUTOR.addToQueue(illuminatiTemplateInterfaceModel);
        } catch (Exception ex) {
            ILLUMINATI_DATA_EXECUTOR_LOGGER.debug("error : check your broker. ("+ex.toString()+")");
        }
    }
}
