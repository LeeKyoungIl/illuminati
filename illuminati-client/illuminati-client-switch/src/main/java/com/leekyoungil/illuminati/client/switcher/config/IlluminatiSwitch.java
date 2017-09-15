package com.leekyoungil.illuminati.client.switcher.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

@Component
@RefreshScope
@PropertySource(value={"classpath:illuminati.yml", "classpath:illuminati.properties"}, ignoreResourceNotFound = true)
@ConfigurationProperties(prefix = "illuminati")
public class IlluminatiSwitch {

    @NestedConfigurationProperty
    private String switchValue;

    public static boolean ILLUMINATI_SWITCH_VALUE = true;

    public void setSwitchValue(String switchValue) {
        if ("true".equalsIgnoreCase(switchValue)) {
            IlluminatiSwitch.ILLUMINATI_SWITCH_VALUE = true;
        } else {
            IlluminatiSwitch.ILLUMINATI_SWITCH_VALUE = false;
        }
    }

    //
//    public static boolean ILLUMINATI_SWITCH_VALUE = true;
//
//    @Value("${illuminatiSwitchValue}")
//    public void setIlluminatiSwitchValue(String illuminatiSwitchValue) {
//        if ("true".equalsIgnoreCase(illuminatiSwitchValue)) {
//            IlluminatiSwitch.ILLUMINATI_SWITCH_VALUE = true;
//        } else {
//            IlluminatiSwitch.ILLUMINATI_SWITCH_VALUE = false;
//        }
//    }
}