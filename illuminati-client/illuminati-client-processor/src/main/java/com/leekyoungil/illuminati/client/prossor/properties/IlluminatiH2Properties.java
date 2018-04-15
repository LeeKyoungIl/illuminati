package com.leekyoungil.illuminati.client.prossor.properties;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.leekyoungil.illuminati.common.properties.IlluminatiBaseProperties;
import com.leekyoungil.illuminati.common.util.StringObjectUtils;

import java.util.Properties;

/**
 * This class is used where 'IlluminatiPropertiesImpl' is not used.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class IlluminatiH2Properties extends IlluminatiBaseProperties {

    private String debug = "false";

    public IlluminatiH2Properties(final Properties prop) {
        super(prop);
    }

    @Override public String getDebug() {
        return StringObjectUtils.isValid(this.debug) ? this.debug : "false";
    }
}
