package com.leekyoungil.illuminati.client.prossor.util;

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
}
