package com.leekyoungil.illuminati.common.properties;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.lang.reflect.Modifier;
import java.util.concurrent.atomic.AtomicBoolean;

abstract public class IlluminatiConstant {

    public static boolean ILLUMINATI_DEBUG = false;

    public static boolean ILLUMINATI_SWITCH_ACTIVATION = false;
    public static AtomicBoolean ILLUMINATI_SWITCH_VALUE = new AtomicBoolean(false);

    protected static final String[] PROPERTIES_KEYS;

    static {
        PROPERTIES_KEYS = new String[]{"parentModuleName", "samplingRate"
                , "broker", "clusterList", "virtualHost", "topic", "queueName"
                , "userName", "password", "isAsync", "isCompression"
                , "performance", "debug", "chaosBomber"};
    }

    public static final Gson ILLUMINATI_GSON_OBJ = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().excludeFieldsWithModifiers(Modifier.TRANSIENT).serializeNulls().create();
}
