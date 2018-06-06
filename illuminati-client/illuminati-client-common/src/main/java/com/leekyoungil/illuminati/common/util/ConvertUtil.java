package com.leekyoungil.illuminati.common.util;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

public class ConvertUtil {

    public static Map<String, String> getClientInfoFromHttpRequest (final HttpServletRequest request) {
        if (request == null) {
            return null;
        }

        final Map<String, String> clientInfoMap = new HashMap<String, String>();
        clientInfoMap.put("clientIp", request.getHeader("X-FORWARDED-FOR"));
        clientInfoMap.put("path", request.getRequestURI());
        clientInfoMap.put("remoteAddr", request.getRemoteAddr());
        clientInfoMap.put("queryString", request.getQueryString());

        // some frameworks (ex. grails) we can't be find requestUri by getRequestURI.
        // so add this line for get requestUri
        final Object anotherPath = request.getAttribute("javax.servlet.forward.request_uri");
        if (anotherPath != null) {
            clientInfoMap.put("anotherPath", anotherPath.toString());
        }

        return clientInfoMap;
    }

    public static Map<String, Object> getStaticInfoFromHttpRequest (final HttpServletRequest request) {
        final Map<String, Object> staticInfoMap = new HashMap<String, Object>();
        staticInfoMap.put("domain", request.getServerName());
        staticInfoMap.put("serverPort", request.getLocalPort());

        if (request.getAttribute("ChaosBomber") != null && "true".equals(request.getAttribute("ChaosBomber").toString())) {
            request.setAttribute("ChaosBomber", null);
            staticInfoMap.put("ChaosBomber" , true);
        }

        return staticInfoMap;
    }

    public static boolean getChaosBomberFromHttpRequest (final HttpServletRequest request) {
        if (request.getAttribute("ChaosBomber") != null && "true".equals(request.getAttribute("ChaosBomber").toString())) {
            request.setAttribute("ChaosBomber", null);
            return true;
        }

        return false;
    }

    public static <K, V> Map<K, V> castToMapOf(Class<K> clazzK, Class<V> clazzV, Map<?, ?> map) {
        for (Map.Entry<?, ?> e: map.entrySet()) {
            checkCast(clazzK, e.getKey());
            checkCast(clazzV, e.getValue());
        }

        @SuppressWarnings("unchecked")
        Map<K, V> result = (Map<K, V>) map;
        return result;
    }

    private static <T> void checkCast(Class<T> clazz, Object obj) {
        if ( !clazz.isInstance(obj) ) {
            StringBuilder exMessage = new StringBuilder();
            exMessage.append("Expected : " + clazz.getName());
            exMessage.append("Was : " + obj.getClass().getName());
            exMessage.append("Value : " + obj);

            throw new ClassCastException(exMessage.toString());
        }
    }
}
