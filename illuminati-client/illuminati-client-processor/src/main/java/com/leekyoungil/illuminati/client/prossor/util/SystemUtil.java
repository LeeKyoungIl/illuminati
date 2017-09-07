package com.leekyoungil.illuminati.client.prossor.util;

import com.leekyoungil.illuminati.client.prossor.init.IlluminatiClientInit;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

public class SystemUtil {

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
                jvmInfo.put(StringUtils.removeDotAndUpperCase(name), javaSystemProperties.getProperty(name));
            }
        }

        return jvmInfo;
    }

    public static Map<String, Object> getJvmMemoryInfo () {
        final Map<String, Object> jvmInfo = new HashMap<String, Object>();
        jvmInfo.put("jvmUsedMemory", (IlluminatiClientInit.RUNTIME.totalMemory() - IlluminatiClientInit.RUNTIME.freeMemory()) / MB);
        jvmInfo.put("jvmFreeMemory", IlluminatiClientInit.RUNTIME.freeMemory() / MB);
        jvmInfo.put("jvmTotalMemory", IlluminatiClientInit.RUNTIME.totalMemory() / MB);
        jvmInfo.put("jvmMaxMemory", IlluminatiClientInit.RUNTIME.maxMemory() / MB);

        return jvmInfo;
    }

    public static String generateGlobalTransactionId (final HttpServletRequest request) {
        Object id = request.getAttribute("illuminatiProcId");
        if (id == null || StringUtils.isValid(id.toString()) == false) {
            id = StringUtils.generateId(new Date().getTime(), "illuminatiProcId");
            request.setAttribute("illuminatiProcId", id);
        }

        return StringUtils.isValid(id.toString()) ? (String) id : null;
    }
}
