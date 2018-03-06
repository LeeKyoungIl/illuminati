package com.leekyoungil.illuminati.client.switcher.properties;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.leekyoungil.illuminati.common.constant.IlluminatiConstant;
import com.leekyoungil.illuminati.common.properties.IlluminatiProperties;
import com.leekyoungil.illuminati.common.util.StringObjectUtils;

import java.lang.reflect.Field;
import java.util.Properties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class IlluminatiSwitchPropertiesImpl implements IlluminatiProperties {

    private String illuminatiSwitchValueURL;
    private String illuminatiSwitchValueURLCheckInterval;

    public IlluminatiSwitchPropertiesImpl () { }

    @Override public void setProperties(final Properties prop) {
        final String keys = "illuminatiSwitchValueURL";

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
    public String getDebug() {
        return null;
    }

    @Override
    public String getSamplingRate() {
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

    @Override public String getIlluminatiSwitchValueURL() {
        return StringObjectUtils.isValid(this.illuminatiSwitchValueURL) ? this.illuminatiSwitchValueURL : "false";
    }

    @Override
    public String getBackTableReset() {
        return null;
    }

    public String getIlluminatiSwitchValueURLCheckInterval() {
        return StringObjectUtils.isValid(this.illuminatiSwitchValueURLCheckInterval)
                ? this.illuminatiSwitchValueURLCheckInterval
                : IlluminatiConstant.BASIC_ILLUMINATI_SWITCH_VALUE_CHECK_INTERVAL;
    }
}
