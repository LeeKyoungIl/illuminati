package me.phoboslabs.illuminati.common.constant;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import me.phoboslabs.illuminati.common.dto.enums.IlluminatiStorageType;
import me.phoboslabs.illuminati.common.properties.IlluminatiJsonCodeProperties;
import me.phoboslabs.illuminati.common.util.PropertiesUtil;

import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

abstract public class IlluminatiConstant {

    public static final DateFormat DATE_FORMAT_EVENT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());

    public static Map<String, Thread> SYSTEM_THREAD_MAP = new HashMap<String, Thread>();

    public static boolean ILLUMINATI_DEBUG = false;

    public static boolean ILLUMINATI_SWITCH_ACTIVATION = false;

    public static IlluminatiStorageType ILLUMINATI_BACKUP_STORAGE_TYPE = null;
    public static boolean ILLUMINATI_BACKUP_ACTIVATION = false;

    public static AtomicBoolean ILLUMINATI_SWITCH_VALUE = new AtomicBoolean(false);

    public final static String BASIC_ILLUMINATI_SWITCH_VALUE_CHECK_INTERVAL = "5000";

    public static final String[] PROPERTIES_KEYS;

    static {
        PROPERTIES_KEYS = new String[]{"parentModuleName", "samplingRate"
                , "broker", "clusterList", "virtualHost", "topic", "queueName"
                , "userName", "password", "isAsync", "isCompression"
                , "performance", "debug", "chaosBomber"};
    }

    public static final Gson ILLUMINATI_GSON_OBJ = new GsonBuilder().serializeNulls().excludeFieldsWithoutExposeAnnotation().excludeFieldsWithModifiers(Modifier.TRANSIENT).create();

    public final static String PROFILES_PHASE = System.getProperty("spring.profiles.active");

    public final static List<String> CONFIG_FILE_EXTENSTIONS = Arrays.asList(new String[] { "properties", "yml", "yaml" });
    public final static List<String> BASIC_CONFIG_FILES = Arrays.asList(new String[] { "application.properties", "application.yml", "application.yaml" });

    public final static ObjectMapper YAML_MAPPER = new ObjectMapper(new YAMLFactory());

    static {
        YAML_MAPPER.setVisibility(
                YAML_MAPPER.getSerializationConfig().getDefaultVisibilityChecker()
                        .withFieldVisibility(JsonAutoDetect.Visibility.ANY)
                        .withGetterVisibility(JsonAutoDetect.Visibility.NONE)
                        .withSetterVisibility(JsonAutoDetect.Visibility.NONE)
                        .withCreatorVisibility(JsonAutoDetect.Visibility.NONE)
        );
    }

    public final static ObjectMapper BASIC_OBJECT_MAPPER = new ObjectMapper();

    public final static IlluminatiJsonCodeProperties JSON_STATUS_CODE = PropertiesUtil.getIlluminatiProperties(IlluminatiJsonCodeProperties.class, "jsonStatusCode");

    public final static Type TYPE_FOR_TYPE_TOKEN = new TypeToken<Map<String, Object>>(){}.getType();

    public final static String BASE_CHARSET = "UTF-8";

    public final static String BASIC_PACKAGE_TYPE = "default";
}
