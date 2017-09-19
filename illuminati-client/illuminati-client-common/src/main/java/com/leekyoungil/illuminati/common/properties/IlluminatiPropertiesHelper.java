package com.leekyoungil.illuminati.common.properties;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.leekyoungil.illuminati.common.util.StringObjectUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.processing.Messager;
import javax.tools.Diagnostic;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

public class IlluminatiPropertiesHelper {

    private final static Logger FILEUTIL_LOGGER = LoggerFactory.getLogger(IlluminatiPropertiesHelper.class);
    private final static List<String> CONFIG_FILE_EXTENSTIONS = Arrays.asList(new String[] { "properties", "yml", "yaml" });
    private final static List<String> BASIC_CONFIG_FILES = Arrays.asList(new String[] { "application.properties", "application.yml", "application.yaml" });
    private final static String PROFILES_PHASE = System.getProperty("spring.profiles.active");
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

    public static String getPropertiesValueByKey(Class<? extends IlluminatiProperties> clazz, Messager messager, final String configPropertiesFileName, final String key) {
        final IlluminatiProperties illuminatiProperties = getIlluminatiProperties(clazz, messager, configPropertiesFileName);
        String propertiesValue = null;

        if (StringObjectUtils.isValid(key) && illuminatiProperties != null) {
            try {
                final String methodName = "get" + key.substring(0, 1).toUpperCase() + key.substring(1);
                final Method getNameMethod = IlluminatiProperties.class.getMethod(methodName);
                propertiesValue = (String) getNameMethod.invoke(illuminatiProperties);
            }
            catch (NoSuchMethodException ex) {
                IlluminatiPropertiesHelper.FILEUTIL_LOGGER.debug("Sorry, unable to find method. (" + ex.toString() + ")");
            }
            catch (IllegalAccessException ex) {
                IlluminatiPropertiesHelper.FILEUTIL_LOGGER.debug("Sorry, unable to access method. (" + ex.toString() + ")");
            }
            catch (InvocationTargetException ex) {
                IlluminatiPropertiesHelper.FILEUTIL_LOGGER.debug("Sorry, unable to package method. (" + ex.toString() + ")");
            }
        }

        return propertiesValue;
    }

    public static IlluminatiProperties getIlluminatiProperties(Class<? extends IlluminatiProperties> clazz, Messager messager, final String configPropertiesFileName) {
        IlluminatiProperties illuminatiProperties = null;

        for (String extension : CONFIG_FILE_EXTENSTIONS) {
            String dotBeforeExtension = ".";

            if (StringObjectUtils.isValid(PROFILES_PHASE)) {
                dotBeforeExtension = "-" + PROFILES_PHASE + ".";
            }

            final String fullFileName = configPropertiesFileName + dotBeforeExtension + extension;
            illuminatiProperties = getIlluminatiPropertiesByFile(clazz, messager, fullFileName);

            if (illuminatiProperties != null) {
                break;
            }
        }

        if (illuminatiProperties == null) {
            illuminatiProperties = getIlluminatiPropertiesFromBasicFiles(clazz, messager);
        }

        if (illuminatiProperties == null) {
            if (messager != null) {
                messager.printMessage(Diagnostic.Kind.WARNING, "Sorry, unable to find " + configPropertiesFileName);
            }

            IlluminatiPropertiesHelper.FILEUTIL_LOGGER.debug("Sorry, unable to find " + configPropertiesFileName);
        }

        return illuminatiProperties;
    }

    private static IlluminatiProperties getIlluminatiPropertiesFromBasicFiles(Class<? extends IlluminatiProperties> clazz, Messager messager) {
        IlluminatiProperties illuminatiProperties = null;

        for (String fileName : BASIC_CONFIG_FILES) {
            illuminatiProperties = getIlluminatiPropertiesByFile(clazz, messager, fileName);

            if (illuminatiProperties != null) {
                return illuminatiProperties;
            }
        }

        return null;
    }

    private static IlluminatiProperties getIlluminatiPropertiesByFile(Class<? extends IlluminatiProperties> clazz, Messager messager, final String configPropertiesFileName) {
        final InputStream input = IlluminatiPropertiesHelper.class.getClassLoader().getResourceAsStream(configPropertiesFileName);
        IlluminatiProperties illuminatiProperties = null;

        if (input == null) {
            return null;
        }

        try {
            if (configPropertiesFileName.indexOf(".yml") > -1 || configPropertiesFileName.indexOf(".yaml") > -1) {
                ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
                mapper.setVisibility(mapper.getSerializationConfig()
                        .getDefaultVisibilityChecker()
                        .withFieldVisibility(JsonAutoDetect.Visibility.ANY)
                        .withGetterVisibility(JsonAutoDetect.Visibility.NONE)
                        .withSetterVisibility(JsonAutoDetect.Visibility.NONE)
                        .withCreatorVisibility(JsonAutoDetect.Visibility.NONE));
                illuminatiProperties = mapper.readValue(input, clazz);
            }
            else {
                final Properties prop = new Properties();
                prop.load(input);

                if (prop == null) {
                    if (messager != null) {
                        messager.printMessage(Diagnostic.Kind.ERROR, "Sorry, unable to convert properties file to Properties. (" + configPropertiesFileName + ")");
                    }

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
        }
        catch (IOException ex) {
            if (messager != null) {
                messager.printMessage(Diagnostic.Kind.WARNING, "Sorry, something is wrong in read process. (" + ex.toString() + ")");
            }

            IlluminatiPropertiesHelper.FILEUTIL_LOGGER.debug("Sorry, something is wrong in read process. (" + ex.toString() + ")");
        }
        finally {
            if (input != null) {
                try {
                    input.close();
                }
                catch (IOException ex) {
                    if (messager != null) {
                        messager.printMessage(Diagnostic.Kind.WARNING, "Sorry, something is wrong in close InputStream process. (" + ex.toString() + ")");
                    }

                    IlluminatiPropertiesHelper.FILEUTIL_LOGGER.debug("Sorry, something is wrong in close InputStream process. (" + ex.toString() + ")");
                }
            }
        }

        return illuminatiProperties;
    }
}
