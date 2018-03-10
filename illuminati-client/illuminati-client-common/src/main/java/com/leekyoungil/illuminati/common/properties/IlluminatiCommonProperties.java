package com.leekyoungil.illuminati.common.properties;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.leekyoungil.illuminati.common.constant.IlluminatiConstant;
import com.leekyoungil.illuminati.common.util.StringObjectUtils;

import java.lang.reflect.Field;
import java.util.Properties;

/**
 * This class is used where 'IlluminatiPropertiesImpl' is not used.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class IlluminatiCommonProperties implements IlluminatiProperties {

    private String debug;
    private String backTableReset;

    @Override
    public void setProperties(final Properties prop) {
        for (String keys : IlluminatiConstant.PROPERTIES_KEYS) {
            final String value = prop.getProperty(keys);
            if (prop.containsKey(keys) && !value.isEmpty()) {
                try {
                    final Field field = this.getClass().getDeclaredField(keys);
                    field.setAccessible(true);
                    field.set(this, value);
                } catch (IllegalAccessException e) {
                    // ignore
                    //e.printStackTrace();
                } catch (NoSuchFieldException e) {
                    // ignore
                    //e.printStackTrace();
                }
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

    @Override
    public String getBackTableReset() {
        return StringObjectUtils.isValid(this.backTableReset) ? this.backTableReset : "false";
    }
}
