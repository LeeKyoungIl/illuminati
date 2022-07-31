package me.phoboslabs.illuminati.common.util;

import me.phoboslabs.illuminati.common.properties.IlluminatiBaseProperties;

import java.util.List;
import java.util.Properties;

public class PropertiesTest extends IlluminatiBaseProperties {

    private List<String> test;

    public PropertiesTest() {
    }

    public PropertiesTest(Properties prop) {
        super(prop);
    }

    public void setTest(List<String> test) {
        this.test = test;
    }

    public List<String> getTest() {
        return this.test;
    }
}
