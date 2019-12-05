package me.phoboslabs.illuminati.common.util;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

public class ConvertUtil {

    public static Map<String, String> getClientInfoFromHttpRequest (final HttpServletRequest request) throws Exception {
        if (request == null) {
            throw new Exception("The Request must not be null.");
        }

        final Map<String, String> clientInfoMap = new HashMap<>();
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

    private static final String CHAOS_BOMBER_KEYWORD = "ChaosBomber";

    public static Map<String, Object> getStaticInfoFromHttpRequest (final HttpServletRequest request) {
        final Map<String, Object> staticInfoMap = new HashMap<>();
        staticInfoMap.put("domain", request.getServerName());
        staticInfoMap.put("serverPort", request.getLocalPort());

        if (request.getAttribute(CHAOS_BOMBER_KEYWORD) != null && "true".equals(request.getAttribute(CHAOS_BOMBER_KEYWORD).toString())) {
            request.setAttribute(CHAOS_BOMBER_KEYWORD, null);
            staticInfoMap.put(CHAOS_BOMBER_KEYWORD, true);
        }

        return staticInfoMap;
    }

    public static boolean getChaosBomberFromHttpRequest (final HttpServletRequest request) {
        if (request.getAttribute(CHAOS_BOMBER_KEYWORD) != null && "true".equals(request.getAttribute(CHAOS_BOMBER_KEYWORD).toString())) {
            request.setAttribute(CHAOS_BOMBER_KEYWORD,null);
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
        if (obj != null && clazz.isInstance(obj) == false) {
            StringBuilder exMessage = new StringBuilder()
                                        .append("Expected : " + clazz.getName())
                                        .append("Was : " + obj.getClass().getName())
                                        .append("Value : " + obj);

            throw new ClassCastException(exMessage.toString());
        }
    }

    public static Map<String, Object> convertObjectToMap (Object obj) throws Exception {
        Map<String, Object> map = new HashMap<>();
        final Field[] fields = obj.getClass().getDeclaredFields();
        for(int i=0; i <fields.length; i++){
            fields[i].setAccessible(true);
            try{
                map.put(fields[i].getName(), fields[i].get(obj));
            } catch(Exception e){
                throw new Exception(e.getMessage());
            }
        }
        return map;
    }
}
