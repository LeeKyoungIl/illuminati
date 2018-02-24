package com.leekyoungil.illuminati.common.properties;

import java.util.Properties;

public interface IlluminatiProperties {

    void setProperties(final Properties prop);
    
    String getBroker();

    String getClusterList();

    String getVirtualHost();

    String getTopic();

    String getQueueName();

    String getUserName();

    String getPassword();

    String getIsAsync();

    String getIsCompression();

    String getPerformance();

    String getDebug();

    String getSamplingRate();

    String getChaosBomber();

    String getParentModuleName();

    String getIlluminatiSwitchValueURL();
}
