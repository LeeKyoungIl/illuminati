package com.leekyoungil.illuminati.common.util;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

public class SystemUtil {

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
}
