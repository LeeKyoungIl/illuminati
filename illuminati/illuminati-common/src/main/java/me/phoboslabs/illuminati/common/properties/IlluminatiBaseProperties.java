package me.phoboslabs.illuminati.common.properties;

import me.phoboslabs.illuminati.common.constant.IlluminatiConstant;
import me.phoboslabs.illuminati.common.util.StringObjectUtils;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.Properties;

public abstract class IlluminatiBaseProperties implements IlluminatiProperties, Serializable {

    protected IlluminatiBaseProperties () {}

    protected IlluminatiBaseProperties (final Properties prop) {
        this.setProperties(prop);
    }

    @Override public void setProperties(final Properties prop) {
        if (prop == null) {
            return;
        }
        for (String keys : IlluminatiConstant.PROPERTIES_KEYS) {
            final String value = prop.getProperty(keys);
            if (prop.containsKey(keys) && StringObjectUtils.isValid(value)) {
                try {
                    final Field field = this.getClass().getDeclaredField(keys);
                    field.setAccessible(true);
                    field.set(this, value);
                } catch (Exception ignored) { }
            }
        }
    }
}
