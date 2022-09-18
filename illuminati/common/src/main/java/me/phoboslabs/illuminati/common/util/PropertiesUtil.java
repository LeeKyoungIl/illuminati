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

package me.phoboslabs.illuminati.common.util;

import static me.phoboslabs.illuminati.common.constant.IlluminatiConstant.BASIC_CONFIG_FILES;
import static me.phoboslabs.illuminati.common.constant.IlluminatiConstant.CONFIG_FILE_EXTENSTIONS;
import static me.phoboslabs.illuminati.common.constant.IlluminatiConstant.PROFILES_PHASE;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import me.phoboslabs.illuminati.common.constant.IlluminatiConstant;
import me.phoboslabs.illuminati.common.properties.IlluminatiProperties;
import me.phoboslabs.illuminati.common.properties.IlluminatiPropertiesHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PropertiesUtil {

    private final static Logger PROPERTIES_UTIL_LOGGER = LoggerFactory.getLogger(PropertiesUtil.class);

    private static List<String> getPropertiesFileNames(String configPropertiesFileName) {
        List<String> fileNames = new ArrayList<>();

        CONFIG_FILE_EXTENSTIONS
            .forEach(extension -> fileNames.add(configPropertiesFileName.concat(getDotBeforeExtension()).concat(extension)));

        return fileNames;
    }

    private static List<String> getPropertiesFileNamesWithoutProfiles(String configPropertiesFileName) {
        List<String> fileNames = new ArrayList<>();

        CONFIG_FILE_EXTENSTIONS
            .forEach(extension -> fileNames.add(configPropertiesFileName.concat(".").concat(extension)));

        return fileNames;
    }

    private static String getDotBeforeExtension() {
        String dotBeforeExtension = ".";

        if (StringObjectUtils.isValid(PROFILES_PHASE)) {
            final int indexOfFirstComma = PROFILES_PHASE.indexOf(",");
            dotBeforeExtension = "-".concat(
                indexOfFirstComma > 0
                    ? PROFILES_PHASE.substring(0, PROFILES_PHASE.indexOf(","))
                    : PROFILES_PHASE
            ).concat(".");
        }

        return dotBeforeExtension;
    }

    public static <T extends IlluminatiProperties> T getIlluminatiProperties(Class<T> clazz, String configPropertiesFileName) {
        T illuminatiProperties = null;

        for (String fullFileName : getPropertiesFileNames(configPropertiesFileName)) {
            illuminatiProperties = getIlluminatiPropertiesByFile(clazz, fullFileName);

            if (illuminatiProperties != null) {
                break;
            }
        }

        if (illuminatiProperties == null) {
            for (String fullFileName : getPropertiesFileNamesWithoutProfiles(configPropertiesFileName)) {
                illuminatiProperties = getIlluminatiPropertiesByFile(clazz, fullFileName);

                if (illuminatiProperties != null) {
                    break;
                }
            }
        }

        if (illuminatiProperties == null) {
            illuminatiProperties = getIlluminatiPropertiesFromBasicFiles(clazz);
        }

        if (illuminatiProperties == null) {
            PROPERTIES_UTIL_LOGGER.debug("Sorry, unable to find " + configPropertiesFileName);
        }

        return illuminatiProperties;
    }

    private static <T extends IlluminatiProperties> T getIlluminatiPropertiesFromBasicFiles(Class<T> clazz) {
        T illuminatiProperties = null;

        for (String fileName : BASIC_CONFIG_FILES) {
            illuminatiProperties = getIlluminatiPropertiesByFile(clazz, fileName);

            if (illuminatiProperties != null) {
                return illuminatiProperties;
            }
        }

        return null;
    }

    private static <T extends IlluminatiProperties> T getIlluminatiPropertiesByFile(Class<T> clazz,
        String configPropertiesFileName) {
        final InputStream input = IlluminatiPropertiesHelper.class.getClassLoader().getResourceAsStream(configPropertiesFileName);
        if (input == null) {
            return null;
        }

        T illuminatiProperties = null;
        try {
            if (configPropertiesFileName.indexOf(".yml") > -1 || configPropertiesFileName.indexOf(".yaml") > -1) {
                illuminatiProperties = IlluminatiConstant.YAML_MAPPER.readValue(input, clazz);
            } else {
                final Properties prop = new Properties();
                prop.load(input);

                try {
                    illuminatiProperties = clazz.newInstance();
                    illuminatiProperties.setProperties(prop);
                } catch (InstantiationException ignore) {
                } catch (IllegalAccessException ignore) {
                }
            }
        } catch (IOException ex) {
            PROPERTIES_UTIL_LOGGER.debug("Sorry, something is wrong in read process. (" + ex.toString() + ")");
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException ex) {
                    PROPERTIES_UTIL_LOGGER.debug(
                        "Sorry, something is wrong in close InputStream process. (" + ex.toString() + ")");
                }
            }
        }

        return illuminatiProperties;
    }

    public static Properties getPropertiesFromFile(String filePath) throws IOException {
        try (InputStream input = PropertiesUtil.class.getClassLoader().getResourceAsStream(filePath)) {
            Properties prop = new Properties();
            prop.load(input);
            return prop;
        } catch (IOException ex) {
            throw new IOException(ex.toString());
        }
    }
}
