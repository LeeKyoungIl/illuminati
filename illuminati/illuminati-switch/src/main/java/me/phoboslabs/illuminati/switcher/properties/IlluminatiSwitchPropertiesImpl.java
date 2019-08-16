package me.phoboslabs.illuminati.switcher.properties;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import me.phoboslabs.illuminati.common.constant.IlluminatiConstant;
import me.phoboslabs.illuminati.common.properties.IlluminatiBaseProperties;
import me.phoboslabs.illuminati.common.util.StringObjectUtils;

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
