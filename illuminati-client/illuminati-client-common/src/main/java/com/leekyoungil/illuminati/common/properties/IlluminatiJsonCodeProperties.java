package com.leekyoungil.illuminati.common.properties;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.Map;
import java.util.Properties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class IlluminatiJsonCodeProperties extends IlluminatiBaseProperties {

    private Map<Integer, String> jsonStatusCode;

    public IlluminatiJsonCodeProperties () {
        super();
    }

    public IlluminatiJsonCodeProperties (final Properties prop) {
        super(prop);
    }

    public String getMessage (int code) {
        return this.jsonStatusCode.get(code);
    }
}
