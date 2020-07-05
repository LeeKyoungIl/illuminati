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

package me.phoboslabs.illuminati.common.util;

import me.phoboslabs.illuminati.common.constant.IlluminatiConstant;
import me.phoboslabs.illuminati.common.dto.enums.IlluminatiTransactionIdType;
import com.sun.management.OperatingSystemMXBean;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import java.lang.management.ManagementFactory;
import java.text.DecimalFormat;
import java.util.*;

public class SystemUtil {

    public static final Logger SYSTEM_UTIL_LOGGER = LoggerFactory.getLogger(SystemUtil.class);

    private final static DecimalFormat DECIMAL_POINT = new DecimalFormat("#.###");
    private final static Runtime RUNTIME = Runtime.getRuntime();
    private final static OperatingSystemMXBean M_BEAN = (com.sun.management.OperatingSystemMXBean)ManagementFactory.getOperatingSystemMXBean();

    private static final List<String> INCLUDE_JAVA_SYSTEM_PROPERTIES = Collections.unmodifiableList(Arrays.asList(
            "user.timezone",
            "user.country.format",
            "user.country",
            "java.home",
            "user.language",
            "file.encoding",
            "catalina.home",
            "PID"));


    private static final int MEGA_BYTE = 1024*1024;
    private static final String JAVA_VM_PREFIX = "java.vm.";

    public static Map<String, Object> getJvmInfo () {
        final Map<String, Object> jvmInfo = new HashMap<String, Object>();
        final Properties javaSystemProperties = System.getProperties();
        for (final String name : javaSystemProperties.stringPropertyNames()) {
            if (name.indexOf(JAVA_VM_PREFIX) > -1 || INCLUDE_JAVA_SYSTEM_PROPERTIES.contains(name)) {
                try {
                    jvmInfo.put(StringObjectUtils.removeDotAndUpperCase(name), javaSystemProperties.getProperty(name));
                } catch (Exception ex) {
                    SYSTEM_UTIL_LOGGER.error(ex.toString(), ex);
                }
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
        jvmInfo.put("jvmCpuUsage", DECIMAL_POINT.format(M_BEAN.getProcessCpuLoad()));
        jvmInfo.put("jvmActiveThreadCount", Thread.activeCount());

        return jvmInfo;
    }

    public static String generateTransactionIdByRequest (final HttpServletRequest request, final IlluminatiTransactionIdType illuminatiTransactionIdType) throws Exception {
        final String keyName = illuminatiTransactionIdType.getValue();

        String trxId = SystemUtil.getValueFromHeaderByKey(request, keyName);
        if (!StringObjectUtils.isValid(trxId) && request != null) {
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

        if (!StringObjectUtils.isValid(trxId)) {
            throw new Exception("trxId must not be null.");
        }

        return trxId;
    }

    public static String getValueFromHeaderByKey (final HttpServletRequest request, final String keyName) throws Exception {
        Object value = null;

        if (request != null && keyName != null) {
            value = request.getHeader(keyName);

            if (value == null) {
                value = request.getAttribute(keyName);
            }
        }

        if (value != null) {
            throw new Exception("value must not be null. (you can ignore this message)");
        }

        return value.toString();
    }

    private static boolean isThreadNameValidated(final String threadName) {
        return !StringUtils.isEmpty(threadName)
                && !IlluminatiConstant.SYSTEM_THREAD_MAP.containsKey(threadName);
    }

    public static void createSystemThread (final Runnable runnable, final String threadName) {
        if (runnable != null && isThreadNameValidated(threadName)) {
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

            if (IlluminatiConstant.SYSTEM_THREAD_MAP.containsKey(threadName)) {
                SYSTEM_UTIL_LOGGER.warn(threadName + " thread is already exists.");
            }
        }
    }

    public static void createThreadStatusDebugThread () {
        // debug illuminati buffer queue
        if (IlluminatiConstant.ILLUMINATI_DEBUG && SYSTEM_UTIL_LOGGER.isInfoEnabled()) {
            final Runnable threadCheckRunnable = () -> {
                while (true) {
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
                    } catch (InterruptedException ignore) {}
                }
            };

            SystemUtil.createSystemThread(threadCheckRunnable, "ILLUMINATI_DEBUG_THREAD");
        }
    }

    public static boolean classExist(final String className) {
        try {
            Class.forName(className);
        } catch (ClassNotFoundException e) {
            //my class isn't there!
            return false;
        }
        return true;
    }
}
