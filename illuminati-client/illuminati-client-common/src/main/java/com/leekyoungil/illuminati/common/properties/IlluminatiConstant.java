package com.leekyoungil.illuminati.common.properties;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.lang.reflect.Modifier;

abstract public class IlluminatiConstant {

    public static boolean ILLUMINATI_DEBUG = false;

    public static boolean ILLUMINATI_SWITCH_ACTIVATION = false;
    public static boolean ILLUMINATI_SWITCH_VALUE = false;

    protected static final String[] PROPERTIES_KEYS;

    static {
        PROPERTIES_KEYS = new String[]{"parentModuleName", "samplingRate"
                , "broker", "clusterList", "virtualHost", "topic", "queueName"
                , "userName", "password", "isAsync", "isCompression"
                , "performance", "debug", "chaosBomber"};
    }

    public static final Gson ILLUMINATI_GSON_OBJ = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().excludeFieldsWithModifiers(Modifier.TRANSIENT).serializeNulls().create();
}
