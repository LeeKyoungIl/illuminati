package com.leekyoungil.illuminati.common.util;

import com.leekyoungil.illuminati.common.constant.IlluminatiConstant;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

public class SystemUtil {

    private static final Logger SYSTEM_UTIL_LOGGER = LoggerFactory.getLogger(SystemUtil.class);

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


    private static final int MB = 1024*1024;

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
        jvmInfo.put("jvmUsedMemory", (RUNTIME.totalMemory() - RUNTIME.freeMemory()) / MB);
        jvmInfo.put("jvmFreeMemory", RUNTIME.freeMemory() / MB);
        jvmInfo.put("jvmTotalMemory", RUNTIME.totalMemory() / MB);
        jvmInfo.put("jvmMaxMemory", RUNTIME.maxMemory() / MB);

        return jvmInfo;
    }

    public static String generateGlobalTransactionId (final HttpServletRequest request) {
        Object id = request.getAttribute("illuminatiProcId");
        if (id == null || StringObjectUtils.isValid(id.toString()) == false) {
            id = StringObjectUtils
                    .generateId(new Date().getTime(), "illuminatiProcId");
            request.setAttribute("illuminatiProcId", id);
        }

        return StringObjectUtils.isValid(id.toString()) ? (String) id : null;
    }

    public static void createSystemThread (final Runnable runnable, final String threadName) {
        if (runnable == null) {
            SYSTEM_UTIL_LOGGER.warn("Runnable is required.");
            return;
        }

        if (StringUtils.isEmpty(threadName)) {
            SYSTEM_UTIL_LOGGER.warn("threadName is required.");
            return;
        }

        if (IlluminatiConstant.SYSTEM_THREAD_MAP.containsKey(threadName)) {
            SYSTEM_UTIL_LOGGER.warn(threadName + " thread is already exists.");
            return;
        }

        final Thread newThread = new Thread(runnable);

        if (!"debug".equalsIgnoreCase(threadName)) {
            newThread.setName(threadName);
        }
        newThread.setDaemon(true);
        newThread.start();

        IlluminatiConstant.SYSTEM_THREAD_MAP.put(threadName, newThread);
    }

    public static void createThreadStatusDebugThread () {
        // debug illuminati buffer queue
        if (IlluminatiConstant.ILLUMINATI_DEBUG == true) {
            final Runnable threadCheckRunnable = new Runnable() {
                public void run() {
                    while (true) {
                        for(Map.Entry<String, Thread> elem : IlluminatiConstant.SYSTEM_THREAD_MAP.entrySet()){
                            SYSTEM_UTIL_LOGGER.info("threadName : "+elem.getKey()+", ThreadIsAlive : "+elem.getValue().isAlive()+", ThreadNowStatus : "+elem.getValue().getState().name());
                        }

                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            // ignore
                        }
                    }
                }
            };

            SystemUtil.createSystemThread(threadCheckRunnable, "debug");
        }
    }
}
