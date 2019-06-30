package com.leekyoungil.illuminati.client.switcher.properties;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.leekyoungil.illuminati.common.constant.IlluminatiConstant;
import com.leekyoungil.illuminati.common.properties.IlluminatiBaseProperties;
import com.leekyoungil.illuminati.common.properties.IlluminatiProperties;
import com.leekyoungil.illuminati.common.util.StringObjectUtils;

import java.lang.reflect.Field;
import java.util.Properties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class IlluminatiSwitchPropertiesImpl extends IlluminatiBaseProperties {

    private String illuminatiSwitchValueURL;
    private String illuminatiSwitchValueURLCheckInterval;

    public IlluminatiSwitchPropertiesImpl () {
        super();
    }

    public IlluminatiSwitchPropertiesImpl (final Properties prop) {
        super(prop);
    }

    public String getIlluminatiSwitchValueURL() {
        return StringObjectUtils.isValid(this.illuminatiSwitchValueURL) ? this.illuminatiSwitchValueURL : "false";
    }

    public String getIlluminatiSwitchValueURLCheckInterval() {
        return StringObjectUtils.isValid(this.illuminatiSwitchValueURLCheckInterval)
                ? this.illuminatiSwitchValueURLCheckInterval
                : IlluminatiConstant.BASIC_ILLUMINATI_SWITCH_VALUE_CHECK_INTERVAL;
    }
}
