package com.leekyoungil.illuminati.client.prossor.init;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.leekyoungil.illuminati.client.prossor.config.IlluminatiProperties;
import com.leekyoungil.illuminati.client.prossor.executor.IlluminatiExecutor;
import com.leekyoungil.illuminati.client.prossor.infra.IlluminatiInfraTemplate;
import com.leekyoungil.illuminati.client.prossor.infra.kafka.impl.KafkaInfraTemplateImpl;
import com.leekyoungil.illuminati.client.prossor.infra.rabbitmq.impl.RabbitmqInfraTemplateImpl;
import com.leekyoungil.illuminati.client.prossor.model.IlluminatiModel;
import com.leekyoungil.illuminati.client.prossor.model.ServerInfo;
import com.leekyoungil.illuminati.client.prossor.util.FileUtils;
import com.leekyoungil.illuminati.client.prossor.util.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class IlluminatiClientInit {

    private static final Logger ILLUMINATI_INIT_LOGGER = LoggerFactory.getLogger(IlluminatiClientInit.class);

    public static final Gson ILLUMINATI_GSON_OBJ = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().excludeFieldsWithModifiers(Modifier.TRANSIENT).serializeNulls().create();
    private static final AtomicInteger SAMPLING_RATE_CHECKER = new AtomicInteger(1);
    private static int SAMPLING_RATE = 20;
    private static int CHAOSBOMBER_NUMBER = (int) (Math.random() * 100) + 1;

    public static AtomicLong ILLUMINATI_TIME_DATA;
    public static IlluminatiInfraTemplate ILLUMINATI_TEMPLATE;
    public static String ILLUMINATI_BROKER;

    public static String PARENT_MODULE_NAME;
    public static ServerInfo SERVER_INFO;
    public static Map<String, Object> JVM_INFO;

    public static Runtime RUNTIME;

    public static Thread ILLUMINATI_TIME_THREAD;

    public synchronized static void init () {
        if (ILLUMINATI_TEMPLATE == null) {
            ILLUMINATI_BROKER = FileUtils.getPropertiesValueByKey(null, "illuminati", "broker");

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

        final String samplingRate = FileUtils.getPropertiesValueByKey(null, "illuminati", "samplingRate");
        SAMPLING_RATE = StringUtils.isValid(samplingRate) ? Integer.valueOf(samplingRate) : SAMPLING_RATE;

        PARENT_MODULE_NAME = FileUtils.getPropertiesValueByKey(null, "illuminati", "parentModuleName");
        SERVER_INFO = new ServerInfo(true);
        JVM_INFO = setJvmInfo();
        RUNTIME = Runtime.getRuntime();

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

        String debug = FileUtils.getPropertiesValueByKey(null, "illuminati", "debug");
        if (StringUtils.isValid(debug)) {
            IlluminatiProperties.ILLUMINATI_DEBUG = Boolean.valueOf(debug);
        }
    }

    private static Map<String, Object> setJvmInfo () {
        final Map<String, Object> jvmInfo = new HashMap<String, Object>();

        final String[] includeJavaSystemProperties = new String[]{
                "user.timezone",
                "user.country.format",
                "user.country",
                "java.home",
                "user.language",
                "file.encoding",
                "catalina.home",
                "PID"};

        final List<String> includeJavaSystemPropertiesList = Arrays.asList(includeJavaSystemProperties);

        final Properties javaSystemProperties = System.getProperties();
        for (final String name : javaSystemProperties.stringPropertyNames()) {
            if (name.indexOf("java.vm.") > -1 || includeJavaSystemPropertiesList.contains(name)) {
                jvmInfo.put(StringUtils.removeDotAndUpperCase(name), javaSystemProperties.getProperty(name));
            }
        }

        return jvmInfo;
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

    public static Object executeIlluminati (ProceedingJoinPoint pjp, HttpServletRequest request) throws Throwable {
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

    private static void sendToIlluminati (HttpServletRequest request, MethodSignature signature, Object[] args, long elapsedTime, Object output) {
        try {
            final IlluminatiModel illuminatiModel = new IlluminatiModel(request, elapsedTime, signature, output, args);
            IlluminatiExecutor.executeToIlluminati(illuminatiModel);
        } catch (Exception ex) {
            ILLUMINATI_INIT_LOGGER.debug("error : check your broker. ("+ex.toString()+")");
        }
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
    public static Object executeIlluminatiByChaosBomber (ProceedingJoinPoint pjp, HttpServletRequest request) throws Throwable {
        // chaosBomber mode activate at debug mode.
        if (IlluminatiProperties.ILLUMINATI_DEBUG == false) {
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

    private static Map<String, Object> getMethodExecuteResult (ProceedingJoinPoint pjp) {
        final Map<String, Object> originMethodExecute = new HashMap<String, Object>();

        try {
            originMethodExecute.put("result", pjp.proceed());
        } catch (Throwable ex) {
            originMethodExecute.put("throwable", ex);
            ILLUMINATI_INIT_LOGGER.error("error : check your process. ("+ex.toString()+")");
            originMethodExecute.put("result", StringUtils.getExceptionMessageChain(ex));
        }

        return originMethodExecute;
    }
}
