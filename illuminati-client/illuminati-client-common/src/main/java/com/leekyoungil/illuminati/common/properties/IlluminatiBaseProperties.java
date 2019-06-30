package com.leekyoungil.illuminati.common.properties;

import com.leekyoungil.illuminati.common.constant.IlluminatiConstant;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.Properties;

public abstract class IlluminatiBaseProperties implements IlluminatiProperties, Serializable {

    protected IlluminatiBaseProperties () {}

    protected IlluminatiBaseProperties (final Properties prop) {
        this.setProperties(prop);
    }

    @Override public void setProperties(final Properties prop) {
        if (prop != null) {
            for (String keys : IlluminatiConstant.PROPERTIES_KEYS) {
                final String value = prop.getProperty(keys);
                if (prop.containsKey(keys) && !value.isEmpty()) {
                    try {
                        final Field field = this.getClass().getDeclaredField(keys);
                        field.setAccessible(true);
                        field.set(this, value);
                    } catch (Exception ignored) { }
                }
            }
        }
    }
}
