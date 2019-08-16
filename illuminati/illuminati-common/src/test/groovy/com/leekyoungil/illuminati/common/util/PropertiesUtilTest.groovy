package me.phoboslabs.illuminati.common.util

import spock.lang.Specification

class PropertiesUtilTest extends Specification {

    def "read properties file in classpath" () {
        setup:
        String fileName = "test";

        when:
        PropertiesTest propertiesData = PropertiesUtil.getIlluminatiProperties(PropertiesTest.class, fileName);

        then:
        propertiesData != null;
    }
}
