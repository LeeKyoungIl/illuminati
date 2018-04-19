package com.leekyoungil.illuminati.common.properties;

import com.leekyoungil.illuminati.common.util.StringObjectUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Properties;

import static com.leekyoungil.illuminati.common.constant.IlluminatiConstant.*;

public class IlluminatiPropertiesHelper {

    private final static Logger FILEUTIL_LOGGER = LoggerFactory.getLogger(IlluminatiPropertiesHelper.class);

    private final static String ILLUMINATI_SWITCH_CONFIGURATION_CLASS_NAME = "com.leekyoungil.illuminati.client.switcher.IlluminatiSwitch";

    /**
     * Spring Cloud Config is active properties.
     *
     * @return boolean
     */
    public static boolean isIlluminatiSwitcherActive() {
        boolean isIlluminatiSwitcherActive = true;

        try {
            Class.forName(ILLUMINATI_SWITCH_CONFIGURATION_CLASS_NAME);
        }
        catch (ClassNotFoundException e) {
            //my class isn't there!
            isIlluminatiSwitcherActive = false;
        }

        return isIlluminatiSwitcherActive;
    }

    public static String getPropertiesValueByKey(final Class<? extends IlluminatiProperties> clazz, final String configPropertiesFileName, final String key, final String defaultValue) {
        final IlluminatiProperties illuminatiProperties = getIlluminatiProperties(clazz, configPropertiesFileName);
        String propertiesValue = null;

        if (StringObjectUtils.isValid(key) && illuminatiProperties != null) {
            try {
                final String methodName = "get" + key.substring(0, 1).toUpperCase() + key.substring(1);
                final Method getNameMethod = IlluminatiProperties.class.getMethod(methodName);
                propertiesValue = (String) getNameMethod.invoke(illuminatiProperties);
            }
            catch (Exception ex) {
                IlluminatiPropertiesHelper.FILEUTIL_LOGGER.debug("Sorry, unable to find method. (" + ex.toString() + ")");
            }
        }

        return (StringObjectUtils.isValid(propertiesValue)) ? propertiesValue : defaultValue;
    }

    public static <T extends IlluminatiProperties> T getIlluminatiProperties(final Class<T> clazz, final String configPropertiesFileName) {
        T illuminatiProperties = null;

        for (String extension : CONFIG_FILE_EXTENSTIONS) {
            String dotBeforeExtension = ".";

            if (StringObjectUtils.isValid(PROFILES_PHASE)) {
                dotBeforeExtension = "-" + PROFILES_PHASE + ".";
            }

            final String fullFileName = configPropertiesFileName + dotBeforeExtension + extension;
            illuminatiProperties = getIlluminatiPropertiesByFile(clazz, fullFileName);

            if (illuminatiProperties != null) {
                break;
            }
        }

        if (illuminatiProperties == null) {
            illuminatiProperties = getIlluminatiPropertiesFromBasicFiles(clazz);
        }

        if (illuminatiProperties == null) {
            IlluminatiPropertiesHelper.FILEUTIL_LOGGER.debug("Sorry, unable to find " + configPropertiesFileName);
        }

        return illuminatiProperties;
    }

    private static <T extends IlluminatiProperties> T getIlluminatiPropertiesFromBasicFiles(final Class<T> clazz) {
        T illuminatiProperties = null;

        for (String fileName : BASIC_CONFIG_FILES) {
            illuminatiProperties = getIlluminatiPropertiesByFile(clazz, fileName);

            if (illuminatiProperties != null) {
                return illuminatiProperties;
            }
        }

        return null;
    }

    private static <T extends IlluminatiProperties> T getIlluminatiPropertiesByFile(final Class<T> clazz, final String configPropertiesFileName) {
        final InputStream input = IlluminatiPropertiesHelper.class.getClassLoader().getResourceAsStream(configPropertiesFileName);
        T illuminatiProperties = null;

        if (input == null) {
            return null;
        }

        try {
            if (configPropertiesFileName.indexOf(".yml") > -1 || configPropertiesFileName.indexOf(".yaml") > -1) {
                illuminatiProperties = YAML_MAPPER.readValue(input, clazz);
            } else {
                final Properties prop = new Properties();
                prop.load(input);

                if (prop == null) {
                    IlluminatiPropertiesHelper.FILEUTIL_LOGGER.debug("Sorry, unable to convert properties file to Properties. (" + configPropertiesFileName + ")");

                    return null;
                }

                try {
                    illuminatiProperties = clazz.newInstance();
                    illuminatiProperties.setProperties(prop);
                } catch (InstantiationException e) {
                    // ignore
                } catch (IllegalAccessException e) {
                    // ignore
                }
            }
        } catch (IOException ex) {
            IlluminatiPropertiesHelper.FILEUTIL_LOGGER.debug("Sorry, something is wrong in read process. (" + ex.toString() + ")");
        } finally {
            if (input != null) {
                try {
                    input.close();
                }
                catch (IOException ex) {
                    IlluminatiPropertiesHelper.FILEUTIL_LOGGER.debug("Sorry, something is wrong in close InputStream process. (" + ex.toString() + ")");
                }
            }
        }

        return illuminatiProperties;
    }

    public static List<?> getPropertiesListByKey(Class<?> clazz, String key) {

    }
}
