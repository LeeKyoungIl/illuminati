package me.phoboslabs.illuminati.common.properties;

import me.phoboslabs.illuminati.common.util.PropertiesUtil;
import me.phoboslabs.illuminati.common.util.StringObjectUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;

public class IlluminatiPropertiesHelper {

    private final static Logger FILE_UTIL_LOGGER = LoggerFactory.getLogger(IlluminatiPropertiesHelper.class);

    private final static String ILLUMINATI_SWITCH_CONFIGURATION_CLASS_NAME = "me.phoboslabs.illuminati.client.switcher.IlluminatiSwitch";

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
        final IlluminatiProperties illuminatiProperties = PropertiesUtil.getIlluminatiProperties(clazz, configPropertiesFileName);
        String propertiesValue = null;

        if (StringObjectUtils.isValid(key) && illuminatiProperties != null) {
            try {
                final String methodName = "get" + key.substring(0, 1).toUpperCase() + key.substring(1);
                final Method getNameMethod = clazz.getMethod(methodName);
                propertiesValue = (String) getNameMethod.invoke(illuminatiProperties);
            }
            catch (Exception ex) {
                FILE_UTIL_LOGGER.debug("Sorry, unable to find method. (" + ex.toString() + ")");
            }
        }

        return (StringObjectUtils.isValid(propertiesValue)) ? propertiesValue : defaultValue;
    }
}
