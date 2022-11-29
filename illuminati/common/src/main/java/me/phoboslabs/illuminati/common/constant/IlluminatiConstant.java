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

package me.phoboslabs.illuminati.common.constant;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import me.phoboslabs.illuminati.common.dto.enums.IlluminatiStorageType;
import me.phoboslabs.illuminati.common.properties.IlluminatiJsonCodeProperties;
import me.phoboslabs.illuminati.common.util.PropertiesUtil;

abstract public class IlluminatiConstant {

    public static final String PROFILES_PHASE = System.getProperty("spring.profiles.active");

    public static final DateTimeFormatter DATE_FORMAT_EVENT = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss",
        Locale.getDefault());

    public static Map<String, Thread> SYSTEM_THREAD_MAP = new HashMap<>();

    public static boolean ILLUMINATI_DEBUG = false;

    public static boolean ILLUMINATI_SWITCH_ACTIVATION = false;

    public static IlluminatiStorageType ILLUMINATI_BACKUP_STORAGE_TYPE = null;
    public static boolean ILLUMINATI_BACKUP_ACTIVATION = false;

    public static AtomicBoolean ILLUMINATI_SWITCH_VALUE = new AtomicBoolean(false);

    public static final String BASIC_ILLUMINATI_SWITCH_VALUE_CHECK_INTERVAL = "5000";

    public static final List<String> PROPERTIES_KEYS;
    public final static List<String> CONFIG_FILE_EXTENSTIONS;
    public static final List<String> BASIC_CONFIG_FILES;

    static {
        PROPERTIES_KEYS = Collections.unmodifiableList(Arrays.asList("parentModuleName", "samplingRate"
            , "broker", "clusterList", "virtualHost", "topic", "queueName"
            , "userName", "password", "isAsync", "isCompression", "compressionType"
            , "performance", "debug", "chaosBomber"));

        CONFIG_FILE_EXTENSTIONS = Collections.unmodifiableList(Arrays.asList("properties", "yml", "yaml"));
        BASIC_CONFIG_FILES = Collections.unmodifiableList(
            Arrays.asList("application.properties", "application.yml", "application.yaml"));
    }

    public static final Gson ILLUMINATI_GSON_OBJ = new GsonBuilder().serializeNulls().excludeFieldsWithoutExposeAnnotation()
        .excludeFieldsWithModifiers(Modifier.TRANSIENT).create();
    public static final Gson ILLUMINATI_GSON_EXCLUDE_NULL_OBJ = new GsonBuilder().excludeFieldsWithModifiers(Modifier.TRANSIENT)
        .create();

    public static final ObjectMapper YAML_MAPPER = new ObjectMapper(new YAMLFactory());

    static {
        YAML_MAPPER.setVisibility(
            YAML_MAPPER.getSerializationConfig().getDefaultVisibilityChecker()
                .withFieldVisibility(JsonAutoDetect.Visibility.ANY)
                .withGetterVisibility(JsonAutoDetect.Visibility.NONE)
                .withSetterVisibility(JsonAutoDetect.Visibility.NONE)
                .withCreatorVisibility(JsonAutoDetect.Visibility.NONE)
        );
    }

    public static final ObjectMapper BASIC_OBJECT_MAPPER = new ObjectMapper();

    public static final ObjectMapper BASIC_OBJECT_STRING_MAPPER;

    static {
        BASIC_OBJECT_STRING_MAPPER = new ObjectMapper();
        BASIC_OBJECT_STRING_MAPPER.configure(SerializationFeature.INDENT_OUTPUT, true);
        BASIC_OBJECT_STRING_MAPPER.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
    }

    public static final IlluminatiJsonCodeProperties JSON_STATUS_CODE = PropertiesUtil.getIlluminatiProperties(
        IlluminatiJsonCodeProperties.class, "jsonStatusCode");

    public static final Type TYPE_FOR_TYPE_TOKEN = new TypeToken<Map<String, Object>>() {
    }.getType();

    public static final String BASE_CHARSET = "UTF-8";

    public static final String BASIC_PACKAGE_TYPE = "default";
}
