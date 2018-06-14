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
public class IlluminatiCommonProperties extends IlluminatiBaseProperties {

    private String debug = "false";

    public IlluminatiCommonProperties () {
        super();
    }

    public IlluminatiCommonProperties (final Properties prop) {
        super(prop);
    }

    public String getDebug () {
        return StringObjectUtils.isValid(this.debug) ? this.debug : "false";
    }
}
