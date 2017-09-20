package com.leekyoungil.illuminati.common.constant;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

abstract public class IlluminatiConstant {

    public static Map<String, Thread> SYSTEM_THREAD_MAP = new HashMap<String, Thread>();

    public static boolean ILLUMINATI_DEBUG = false;

    public static boolean ILLUMINATI_SWITCH_ACTIVATION = false;
    public static AtomicBoolean ILLUMINATI_SWITCH_VALUE = new AtomicBoolean(false);

    public final static String BASIC_ILLUMINATI_SWITCH_VALUE_CHECK_INTERVAL = "5000";

    protected static final String[] PROPERTIES_KEYS;

    static {
        PROPERTIES_KEYS = new String[]{"parentModuleName", "samplingRate"
                , "broker", "clusterList", "virtualHost", "topic", "queueName"
                , "userName", "password", "isAsync", "isCompression"
                , "performance", "debug", "chaosBomber"};
    }

    public static final Gson ILLUMINATI_GSON_OBJ = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().excludeFieldsWithModifiers(Modifier.TRANSIENT).serializeNulls().create();
}
