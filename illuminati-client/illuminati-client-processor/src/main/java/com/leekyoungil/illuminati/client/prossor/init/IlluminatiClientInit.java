package com.leekyoungil.illuminati.client.prossor.init;

import com.leekyoungil.illuminati.client.prossor.executor.IlluminatiExecutor;
import com.leekyoungil.illuminati.client.prossor.infra.IlluminatiInfraTemplate;
import com.leekyoungil.illuminati.client.prossor.infra.kafka.impl.KafkaInfraTemplateImpl;
import com.leekyoungil.illuminati.client.prossor.infra.rabbitmq.impl.RabbitmqInfraTemplateImpl;
import com.leekyoungil.illuminati.common.dto.IlluminatiModel;
import com.leekyoungil.illuminati.common.dto.RequestHeaderModel;
import com.leekyoungil.illuminati.common.dto.ServerInfo;
import com.leekyoungil.illuminati.common.properties.IlluminatiPropertiesHelper;
import com.leekyoungil.illuminati.client.prossor.properties.IlluminatiPropertiesImpl;
import com.leekyoungil.illuminati.common.properties.IlluminatiConstant;
import com.leekyoungil.illuminati.common.util.ConvertUtil;
import com.leekyoungil.illuminati.common.util.StringObjectUtils;
import com.leekyoungil.illuminati.common.util.SystemUtil;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class IlluminatiClientInit {

    private static final Logger ILLUMINATI_INIT_LOGGER = LoggerFactory.getLogger(IlluminatiClientInit.class);

    private static final AtomicInteger SAMPLING_RATE_CHECKER = new AtomicInteger(1);
    private static int SAMPLING_RATE = 20;
    private static int CHAOSBOMBER_NUMBER = (int) (Math.random() * 100) + 1;

    public static AtomicLong ILLUMINATI_TIME_DATA;
    public static IlluminatiInfraTemplate ILLUMINATI_TEMPLATE;
    public static String ILLUMINATI_BROKER;

    public static String PARENT_MODULE_NAME;
    public static ServerInfo SERVER_INFO;
    public static Map<String, Object> JVM_INFO;

    public static Thread ILLUMINATI_TIME_THREAD;

    public synchronized static void init () {
        if (ILLUMINATI_TEMPLATE == null) {
            ILLUMINATI_BROKER = IlluminatiPropertiesHelper.getPropertiesValueByKey(IlluminatiPropertiesImpl.class, null, "illuminati", "broker");

            if ("kafka".equals(ILLUMINATI_BROKER)) {
                ILLUMINATI_TEMPLATE = new KafkaInfraTemplateImpl("illuminati");
            } else if ("rabbitmq".equals(ILLUMINATI_BROKER)) {
                ILLUMINATI_TEMPLATE = new RabbitmqInfraTemplateImpl("illuminati");
            } else {
                ILLUMINATI_TEMPLATE = null;
                ILLUMINATI_INIT_LOGGER.error("Sorry. check your properties of Illuminati");
                new Exception("Sorry. check your properties of Illuminati");
                return;
            }
        }

        if (ILLUMINATI_TEMPLATE == null || ILLUMINATI_TEMPLATE.canIConnect() == false) {
            return;
        }

        final String samplingRate = IlluminatiPropertiesHelper.getPropertiesValueByKey(IlluminatiPropertiesImpl.class, null, "illuminati", "samplingRate");
        SAMPLING_RATE = StringObjectUtils.isValid(samplingRate) ? Integer.valueOf(samplingRate) : SAMPLING_RATE;

        PARENT_MODULE_NAME = IlluminatiPropertiesHelper.getPropertiesValueByKey(IlluminatiPropertiesImpl.class, null, "illuminati", "parentModuleName");
        SERVER_INFO = new ServerInfo(true);
        // get basic JVM setting info only once.
        JVM_INFO = SystemUtil.getJvmInfo();

        if (ILLUMINATI_TIME_THREAD == null) {
            ILLUMINATI_TIME_DATA = new AtomicLong(0);

            Runnable runnable = new Runnable() {
                public void run() {
                    while (true) {
                        ILLUMINATI_TIME_DATA.incrementAndGet();

                        try {
                            Thread.sleep(1);
                        } catch (InterruptedException e) {
                            ILLUMINATI_INIT_LOGGER.error("InterruptedException on ILLUMINATI_TIME_DATA. ("+e.toString()+")");
                        }
                    }
                }
            };

            ILLUMINATI_TIME_THREAD = new Thread(runnable);
            ILLUMINATI_TIME_THREAD.setName("ILLUMINATI_TIME_THREAD");
            ILLUMINATI_TIME_THREAD.setDaemon(true);
            ILLUMINATI_TIME_THREAD.start();
        }

        String debug = IlluminatiPropertiesHelper.getPropertiesValueByKey(IlluminatiPropertiesImpl.class, null, "illuminati", "debug");
        if (StringObjectUtils.isValid(debug)) {
            IlluminatiConstant.ILLUMINATI_DEBUG = Boolean.valueOf(debug);
        }

        if (IlluminatiPropertiesHelper.isIlluminatiSwitcherActive() == true) {
            IlluminatiConstant.ILLUMINATI_SWITCH_ACTIVATION = true;
        }
    }

    private static boolean checkSamplingRate () {
        //SAMPLING_RATE_CHECKER.compareAndSet(100, 1);

        // sometimes compareAndSet does not work.
        // So add this code. This code forces a reset to 1 if greater than 100.
        if (SAMPLING_RATE_CHECKER.get() > 100) {
            SAMPLING_RATE_CHECKER.set(1);
            return true;
        }

        if (SAMPLING_RATE_CHECKER.getAndIncrement() <= SAMPLING_RATE) {
            return true;
        }

        return false;
    }

    public static Object executeIlluminati (final ProceedingJoinPoint pjp, final HttpServletRequest request) throws Throwable {
        if (IlluminatiConstant.ILLUMINATI_SWITCH_VALUE == false) {
            ILLUMINATI_INIT_LOGGER.debug("iilluminati processor is now off.");
            return pjp.proceed();
        }

        if (ILLUMINATI_TEMPLATE == null || !IlluminatiClientInit.checkSamplingRate()) {
            ILLUMINATI_INIT_LOGGER.debug("ignore illuminati processor.");
            return pjp.proceed();
        }

        final long start = IlluminatiClientInit.ILLUMINATI_TIME_DATA.get();
        final Map<String, Object> originMethodExecute = getMethodExecuteResult(pjp);
        final long elapsedTime = IlluminatiClientInit.ILLUMINATI_TIME_DATA.get() - start;

        final Object output = originMethodExecute.get("result");
        Throwable throwable = null;
        if (originMethodExecute.containsKey("throwable")) {
            throwable = (Throwable) originMethodExecute.get("throwable");
        }

        sendToIlluminati(request, (MethodSignature) pjp.getSignature(), pjp.getArgs(), elapsedTime, output);

        if (throwable != null) {
            throw throwable;
        }

        return output;
    }

    private static void sendToIlluminati (final HttpServletRequest request, final MethodSignature signature, final Object[] args, long elapsedTime, final Object output) {
        try {
            final IlluminatiModel illuminatiModel = new IlluminatiModel(new Date(), elapsedTime, signature, output, args);
            addDataOnIlluminatiModel(illuminatiModel, request);
            IlluminatiExecutor.executeToIlluminati(illuminatiModel);
        } catch (Exception ex) {
            ILLUMINATI_INIT_LOGGER.debug("error : check your broker. ("+ex.toString()+")");
        }
    }

    private static void addDataOnIlluminatiModel (final IlluminatiModel illuminatiModel, final HttpServletRequest request) {
        RequestHeaderModel requestHeaderModel = new RequestHeaderModel(request);
        requestHeaderModel.setGlobalTransactionId(SystemUtil.generateGlobalTransactionId(request));

        illuminatiModel.initReqHeaderInfo(requestHeaderModel);
        illuminatiModel.loadClientInfo(ConvertUtil.getClientInfoFromHttpRequest(request));
        illuminatiModel.staticInfo(ConvertUtil.getStaticInfoFromHttpRequest(request));
        illuminatiModel.isActiveChaosBomber(ConvertUtil.getChaosBomberFromHttpRequest(request));
        illuminatiModel.initBasicJvmInfo(SystemUtil.getJvmInfo());
        illuminatiModel.addBasicJvmMemoryInfo(SystemUtil.getJvmMemoryInfo());
    }

    /**
     * it is only execute on debug mode and activated chaosBomber.
     * can't be use sampling rate.
     *
     * @param pjp
     * @param request
     * @return
     * @throws Throwable
     */
    public static Object executeIlluminatiByChaosBomber (final ProceedingJoinPoint pjp, final HttpServletRequest request) throws Throwable {
        // chaosBomber mode activate at debug mode.
        if (IlluminatiPropertiesImpl.ILLUMINATI_DEBUG == false) {
            return IlluminatiClientInit.executeIlluminati(pjp, request);
        }

        if (ILLUMINATI_TEMPLATE == null) {
            ILLUMINATI_INIT_LOGGER.debug("ignore illuminati processor.");
            return pjp.proceed();
        }

        final long start = IlluminatiClientInit.ILLUMINATI_TIME_DATA.get();
        final Map<String, Object> originMethodExecute = getMethodExecuteResult(pjp);
        final long elapsedTime = IlluminatiClientInit.ILLUMINATI_TIME_DATA.get() - start;

        final Object output = originMethodExecute.get("result");
        Throwable throwable = null;
        if (originMethodExecute.containsKey("throwable")) {
            throwable = (Throwable) originMethodExecute.get("throwable");
        } else if (CHAOSBOMBER_NUMBER == ((int) (Math.random() * 100) + 1)) {
            throwable = new Throwable("Illuminati ChaosBomber Exception Activate");
            request.setAttribute("ChaosBomber", "true");
        }

        sendToIlluminati(request, (MethodSignature) pjp.getSignature(), pjp.getArgs(), elapsedTime, output);

        if (throwable != null) {
            throw throwable;
        }

        return output;
    }

    private static Map<String, Object> getMethodExecuteResult (final ProceedingJoinPoint pjp) {
        final Map<String, Object> originMethodExecute = new HashMap<String, Object>();

        try {
            originMethodExecute.put("result", pjp.proceed());
        } catch (Throwable ex) {
            originMethodExecute.put("throwable", ex);
            ILLUMINATI_INIT_LOGGER.error("error : check your process. ("+ex.toString()+")");
            originMethodExecute.put("result", StringObjectUtils.getExceptionMessageChain(ex));
        }

        return originMethodExecute;
    }
}
