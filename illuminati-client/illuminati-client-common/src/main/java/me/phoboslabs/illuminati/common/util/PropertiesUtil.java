package me.phoboslabs.illuminati.common.util;

import me.phoboslabs.illuminati.common.constant.IlluminatiConstant;
import me.phoboslabs.illuminati.common.properties.IlluminatiProperties;
import me.phoboslabs.illuminati.common.properties.IlluminatiPropertiesHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import static me.phoboslabs.illuminati.common.constant.IlluminatiConstant.*;

public class PropertiesUtil {

    private final static Logger PROPERTIES_UTIL_LOGGER = LoggerFactory.getLogger(PropertiesUtil.class);

    public static List<String> getPropertiesFileNames (final String configPropertiesFileName) {
        List<String> fileNames = new ArrayList<String>();

        for (String extension : CONFIG_FILE_EXTENSTIONS) {
            final String fullFileName = configPropertiesFileName + getDotBeforeExtension() + extension;
            fileNames.add(fullFileName);
        }

        return fileNames;
    }

    public static List<String> getPropertiesFileNamesWithoutProfiles (final String configPropertiesFileName) {
        List<String> fileNames = new ArrayList<String>();

        for (String extension : CONFIG_FILE_EXTENSTIONS) {
            final String fullFileName = configPropertiesFileName + "." + extension;
            fileNames.add(fullFileName);
        }

        return fileNames;
    }

    private static String getDotBeforeExtension () {
        String dotBeforeExtension = ".";

        if (StringObjectUtils.isValid(PROFILES_PHASE)) {
            dotBeforeExtension = "-" + PROFILES_PHASE + ".";
        }

        return dotBeforeExtension;
    }

    public static <T extends IlluminatiProperties> T getIlluminatiProperties(final Class<T> clazz, final String configPropertiesFileName) {
        T illuminatiProperties = null;

        for (String fullFileName : PropertiesUtil.getPropertiesFileNames(configPropertiesFileName)) {
            illuminatiProperties = getIlluminatiPropertiesByFile(clazz, fullFileName);

            if (illuminatiProperties != null) {
                break;
            }
        }

        if (illuminatiProperties == null) {
            for (String fullFileName : PropertiesUtil.getPropertiesFileNamesWithoutProfiles(configPropertiesFileName)) {
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
                illuminatiProperties = IlluminatiConstant.YAML_MAPPER.readValue(input, clazz);
            } else {
                final Properties prop = new Properties();
                prop.load(input);

                if (prop == null) {
                    PROPERTIES_UTIL_LOGGER.debug("Sorry, unable to convert properties file to Properties. (" + configPropertiesFileName + ")");

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
            PROPERTIES_UTIL_LOGGER.debug("Sorry, something is wrong in read process. (" + ex.toString() + ")");
        } finally {
            if (input != null) {
                try {
                    input.close();
                }
                catch (IOException ex) {
                    PROPERTIES_UTIL_LOGGER.debug("Sorry, something is wrong in close InputStream process. (" + ex.toString() + ")");
                }
            }
        }

        return illuminatiProperties;
    }
}
