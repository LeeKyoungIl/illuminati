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

package me.phoboslabs.illuminati.common.properties;

import java.lang.reflect.Method;
import me.phoboslabs.illuminati.common.util.PropertiesUtil;
import me.phoboslabs.illuminati.common.util.StringObjectUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
        } catch (ClassNotFoundException e) {
            //my class isn't there!
            isIlluminatiSwitcherActive = false;
        }

        return isIlluminatiSwitcherActive;
    }

    public static String getPropertiesValueByKey(Class<? extends IlluminatiProperties> clazz, String configPropertiesFileName,
        String key, String defaultValue) {
        final IlluminatiProperties illuminatiProperties = PropertiesUtil.getIlluminatiProperties(clazz, configPropertiesFileName);
        String propertiesValue = null;

        if (StringObjectUtils.isValid(key) && illuminatiProperties != null) {
            try {
                final String methodName = "get" + key.substring(0, 1).toUpperCase() + key.substring(1);
                final Method getNameMethod = clazz.getMethod(methodName);
                propertiesValue = (String) getNameMethod.invoke(illuminatiProperties);
            } catch (Exception ex) {
                FILE_UTIL_LOGGER.debug("Sorry, unable to find method. (" + ex.toString() + ")");
            }
        }

        return (StringObjectUtils.isValid(propertiesValue)) ? propertiesValue : defaultValue;
    }
}
