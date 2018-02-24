package com.leekyoungil.illuminati.common.properties;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.leekyoungil.illuminati.common.util.StringObjectUtils;

import java.lang.reflect.Field;
import java.util.Properties;

/**
 * This class is used where 'IlluminatiPropertiesImpl' is not used.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class IlluminatiCommonProperties implements IlluminatiProperties {

    private String debug;

    @Override
    public void setProperties(Properties prop) {
        final String keys = "debug";

        final String value = prop.getProperty(keys);
        if (prop.containsKey(keys) && !value.isEmpty()) {
            try {
                final Field field = this.getClass().getDeclaredField(keys);
                field.setAccessible(true);
                field.set(this, value);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (NoSuchFieldException e) {
                e.printStackTrace();
            }
        }
    }

    @Override public String getDebug() {
        return StringObjectUtils.isValid(this.debug) ? this.debug : "false";
    }

    @Override
    public String getSamplingRate() {
        return null;
    }

    @Override
    public String getBroker() {
        return null;
    }

    @Override
    public String getClusterList() {
        return null;
    }

    @Override
    public String getVirtualHost() {
        return null;
    }

    @Override
    public String getTopic() {
        return null;
    }

    @Override
    public String getQueueName() {
        return null;
    }

    @Override
    public String getUserName() {
        return null;
    }

    @Override
    public String getPassword() {
        return null;
    }

    @Override
    public String getIsAsync() {
        return null;
    }

    @Override
    public String getIsCompression() {
        return null;
    }

    @Override
    public String getPerformance() {
        return null;
    }

    @Override
    public String getChaosBomber() {
        return null;
    }

    @Override
    public String getParentModuleName() {
        return null;
    }

    @Override
    public String getIlluminatiSwitchValueURL() {
        return null;
    }
}
