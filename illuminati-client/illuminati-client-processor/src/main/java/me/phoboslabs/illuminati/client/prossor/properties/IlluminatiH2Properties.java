package me.phoboslabs.illuminati.client.prossor.properties;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import me.phoboslabs.illuminati.common.properties.IlluminatiBaseProperties;
import me.phoboslabs.illuminati.common.util.StringObjectUtils;

import java.util.Properties;

/**
 * This class is used where 'IlluminatiPropertiesImpl' is not used.
 *
 * Sample
 *  - backTableReset: false
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class IlluminatiH2Properties extends IlluminatiBaseProperties {

    private String backTableReset = "false";

    public IlluminatiH2Properties () {
        super();
    }

    public IlluminatiH2Properties(final Properties prop) {
        super(prop);
    }

    public String getBackTableReset() {
        return StringObjectUtils.isValid(this.backTableReset) ? this.backTableReset : "false";
    }
}
