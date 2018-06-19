package com.leekyoungil.illuminati.common.util;

import com.leekyoungil.illuminati.common.constant.IlluminatiConstant;
import com.leekyoungil.illuminati.common.dto.enums.IlluminatiTransactionIdType;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

public class SystemUtil {

    public static final Logger SYSTEM_UTIL_LOGGER = LoggerFactory.getLogger(SystemUtil.class);

    private final static Runtime RUNTIME = Runtime.getRuntime();

    private static final String[] INCLUDE_JAVA_SYSTEM_PROPERTIES = new String[]{
            "user.timezone",
            "user.country.format",
            "user.country",
            "java.home",
            "user.language",
            "file.encoding",
            "catalina.home",
            "PID"};


    private static final int MEGA_BYTE = 1024*1024;

    public static Map<String, Object> getJvmInfo () {
        final Map<String, Object> jvmInfo = new HashMap<String, Object>();
        final List<String> includeJavaSystemPropertiesList = Arrays.asList(INCLUDE_JAVA_SYSTEM_PROPERTIES);

        final Properties javaSystemProperties = System.getProperties();
        for (final String name : javaSystemProperties.stringPropertyNames()) {
            if (name.indexOf("java.vm.") > -1 || includeJavaSystemPropertiesList.contains(name)) {
                jvmInfo.put(StringObjectUtils.removeDotAndUpperCase(name), javaSystemProperties.getProperty(name));
            }
        }

        return jvmInfo;
    }

    public static Map<String, Object> getJvmMemoryInfo () {
        final Map<String, Object> jvmInfo = new HashMap<String, Object>();
        jvmInfo.put("jvmUsedMemory", (RUNTIME.totalMemory() - RUNTIME.freeMemory()) / MEGA_BYTE);
        jvmInfo.put("jvmFreeMemory", RUNTIME.freeMemory() / MEGA_BYTE);
        jvmInfo.put("jvmTotalMemory", RUNTIME.totalMemory() / MEGA_BYTE);
        jvmInfo.put("jvmMaxMemory", RUNTIME.maxMemory() / MEGA_BYTE);

        return jvmInfo;
    }

    public static String generateTransactionIdByRequest (final HttpServletRequest request, final IlluminatiTransactionIdType illuminatiTransactionIdType) {
        final String keyName = illuminatiTransactionIdType.getValue();

        String trxId = SystemUtil.getValueFromHeaderByKey(request, keyName);
        if (StringObjectUtils.isValid(trxId) == Boolean.FALSE && request != null) {
            switch (illuminatiTransactionIdType) {
                case ILLUMINATI_PROC_ID :
                case ILLUMINATI_G_PROC_ID :
                    trxId = StringObjectUtils.generateId(new Date().getTime(), keyName);
                    request.setAttribute(keyName, trxId);
                    break;

                default :
                    break;
            }
        }

        return (StringObjectUtils.isValid(trxId) == Boolean.TRUE) ? trxId : null;
    }

    public static String getValueFromHeaderByKey (final HttpServletRequest request, final String keyName) {
        Object value = null;

        if (request != null && keyName != null) {
            value = request.getHeader(keyName);

            if (value == null) {
                value = request.getAttribute(keyName);
            }
        }

        return (value != null) ? value.toString() : null;
    }

    public static void createSystemThread (final Runnable runnable, final String threadName) {
        if (runnable != null && StringUtils.isEmpty(threadName) == Boolean.FALSE
                && IlluminatiConstant.SYSTEM_THREAD_MAP.containsKey(threadName) == Boolean.FALSE) {
            final Thread newThread = new Thread(runnable);

            if (!"debug".equalsIgnoreCase(threadName)) {
                newThread.setName(threadName);
            }
            newThread.setDaemon(true);
            newThread.start();

            IlluminatiConstant.SYSTEM_THREAD_MAP.put(threadName, newThread);
        } else {
            if (runnable == null) {
                SYSTEM_UTIL_LOGGER.warn("Runnable is required.");
            }

            if (StringUtils.isEmpty(threadName)) {
                SYSTEM_UTIL_LOGGER.warn("threadName is required.");
            }

            if (IlluminatiConstant.SYSTEM_THREAD_MAP.containsKey(threadName) == Boolean.TRUE) {
                SYSTEM_UTIL_LOGGER.warn(threadName + " thread is already exists.");
            }
        }
    }

    public static void createThreadStatusDebugThread () {
        // debug illuminati buffer queue
        if (IlluminatiConstant.ILLUMINATI_DEBUG == Boolean.TRUE && SYSTEM_UTIL_LOGGER.isInfoEnabled() == Boolean.TRUE) {
            final Runnable threadCheckRunnable = new Runnable() {
                public void run() {
                    while (Boolean.TRUE) {
                        for(Map.Entry<String, Thread> elem : IlluminatiConstant.SYSTEM_THREAD_MAP.entrySet()){
                            SYSTEM_UTIL_LOGGER.info("");
                            SYSTEM_UTIL_LOGGER.info("#########################################################################################################");
                            SYSTEM_UTIL_LOGGER.info("# debug info");
                            SYSTEM_UTIL_LOGGER.info("# -------------------------------------------------------------------------------------------------------");
                            SYSTEM_UTIL_LOGGER.info("# ILLUMINATI_SWITCH_ACTIVATION : " + IlluminatiConstant.ILLUMINATI_SWITCH_ACTIVATION);
                            SYSTEM_UTIL_LOGGER.info("# ILLUMINATI_SWITCH_VALUE : " + IlluminatiConstant.ILLUMINATI_SWITCH_VALUE.get());
                            SYSTEM_UTIL_LOGGER.info("# ILLUMINATI_SWITCH_VALUE_CHECK_INTERVAL : " + IlluminatiConstant.BASIC_ILLUMINATI_SWITCH_VALUE_CHECK_INTERVAL + "ms");
                            SYSTEM_UTIL_LOGGER.info("# threadName : "+elem.getKey()+", ThreadIsAlive : "+elem.getValue().isAlive()+", ThreadNowStatus : "+elem.getValue().getState().name());
                            SYSTEM_UTIL_LOGGER.info("#########################################################################################################");
                            SYSTEM_UTIL_LOGGER.info("");
                        }

                        try {
                            Thread.sleep(10000);
                        } catch (InterruptedException e) {
                            // ignore
                        }
                    }
                }
            };

            SystemUtil.createSystemThread(threadCheckRunnable, "ILLUMINATI_DEBUG_THREAD");
        }
    }
}
