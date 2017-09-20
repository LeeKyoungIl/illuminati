package com.leekyoungil.illuminati.client.prossor.properties;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.leekyoungil.illuminati.common.constant.IlluminatiConstant;
import com.leekyoungil.illuminati.common.properties.IlluminatiProperties;
import com.leekyoungil.illuminati.common.util.StringObjectUtils;

import java.lang.reflect.Field;
import java.util.Properties;

/**
 * Created by leekyoungil (leekyoungil@gmail.com) on 09/17/2017.
 *
 * Sample
 *  - rabbitmq
 *      illuminati.client.broker=rabbitmq
 *      illuminati.client.clusterList=alpha-rabbitmq001.dakao.io:5672,alpha-rabbitmq002.dakao.io:5672,alpha-rabbitmq003.dakao.io:5672,alpha-rabbitmq004.dakao.io:5672,alpha-rabbitmq005.dakao.io:5672
 *      illuminati.client.virtualHost=illuminatiAlphaVhost
 *      illuminati.client.topic=alpha-illuminati-exchange
 *      illuminati.client.userName=illuminati-alpha
 *      illuminati.client.password=illuminati-alpha
 *      illuminati.client.isAsync=true
 *      illuminati.client.isCompression=true
 *      illuminati.client.performance=1
 *      illuminati.client.parentModuleName=apisample
 *      illuminati.client.samplingRate=50
 *
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class IlluminatiPropertiesImpl extends IlluminatiConstant implements IlluminatiProperties {

    private String parentModuleName;
    private String broker;
    private String clusterList;
    private String virtualHost;
    private String topic;
    private String queueName;
    private String userName;
    private String password;
    private String samplingRate;

    private String isAsync;
    private String isCompression;
    private String performance;

    private String debug;

    private String illuminatiSwitchValueURL;

    // * it is very dangerous function. it is activate when debug is true.
    // * after using this function. you must have to re compile.(clean first)
    private String chaosBomber;

    public IlluminatiPropertiesImpl() {}

    @Override public void setProperties(final Properties prop) {
        for (String keys : PROPERTIES_KEYS) {
            final String value = prop.getProperty(keys);
            if (prop.containsKey(keys) && !value.isEmpty()) {
                try {
                    final Field field = this.getClass().getDeclaredField(keys);
                    field.setAccessible(true);
                    field.set(this, value);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (NoSuchFieldException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override public String getBroker() {
        return StringObjectUtils.isValid(this.broker) ? this.broker.toLowerCase() : null;
    }

    @Override public String getClusterList() {
        return this.clusterList;
    }

    @Override public String getVirtualHost() {
        return this.virtualHost;
    }

    @Override public String getTopic() {
        return StringObjectUtils.isValid(this.topic) ? this.topic : "";
    }

    @Override public String getQueueName() {
        return StringObjectUtils.isValid(this.queueName) ? this.queueName : "";
    }

    @Override public String getUserName() {
        return StringObjectUtils.isValid(this.userName) ? this.userName : "";
    }

    @Override public String getPassword() {
        return StringObjectUtils.isValid(this.password) ? this.password : "";
    }

    @Override public String getIsAsync() {
        return StringObjectUtils.isValid(this.isAsync) ? this.isAsync : "";
    }

    @Override public String getIsCompression() {
        return StringObjectUtils.isValid(this.isCompression) ? this.isCompression : "";
    }

    @Override public String getPerformance() {
        return StringObjectUtils.isValid(this.performance) ? this.performance : "";
    }

    @Override public String getDebug() {
        return StringObjectUtils.isValid(this.debug) ? this.debug : "";
    }

    @Override public String getChaosBomber() {
        return StringObjectUtils.isValid(this.chaosBomber) ? this.chaosBomber : "";
    }

    @Override public String getParentModuleName() {
        return StringObjectUtils.isValid(this.parentModuleName) ? this.parentModuleName : "unknown";
    }

    @Override public String getIlluminatiSwitchValueURL() {
        return StringObjectUtils.isValid(this.illuminatiSwitchValueURL) ? this.illuminatiSwitchValueURL : "false";
    }
}
