package com.leekyoungil.illuminati.client.prossor.properties;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.leekyoungil.illuminati.common.constant.IlluminatiConstant;
import com.leekyoungil.illuminati.common.properties.IlluminatiBaseProperties;
import com.leekyoungil.illuminati.common.properties.IlluminatiCommonProperties;
import com.leekyoungil.illuminati.common.properties.IlluminatiProperties;
import com.leekyoungil.illuminati.common.util.StringObjectUtils;

import java.lang.reflect.Field;
import java.util.Properties;

/**
 * Created by leekyoungil (leekyoungil@gmail.com) on 04/16/2018.
 *
 * Sample
 * - broker: rabbitmq
 * - clusterList: pi.leekyoungil.com:5672
 * - virtualHost: illuminatiPiDev
 * - topic: pi-dev-illuminati-exchange
 * - queueName: pi-dev-illuminati-exchange.illuminati
 * - userName: illuminati-dev
 * - password: yourpassword
 * - isAsync: true
 * - isCompression: true
 * - parentModuleName: apisample
 * - samplingRate: 100
 * - performance: 0 // it's only using when you choose kafka.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class IlluminatiPropertiesImpl extends IlluminatiBaseProperties {

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
    // it's only using when you choose kafka.
    private String performance;

    public IlluminatiPropertiesImpl(final Properties prop) {
        super(prop);
    }

    public String getBroker() {
        return StringObjectUtils.isValid(this.broker) ? this.broker.toLowerCase() : null;
    }

    public String getClusterList() {
        return StringObjectUtils.isValid(this.clusterList) ? this.clusterList : null;
    }

    public String getVirtualHost() {
        return StringObjectUtils.isValid(this.virtualHost) ? this.virtualHost : null;
    }

    public String getTopic() {
        return StringObjectUtils.isValid(this.topic) ? this.topic : null;
    }

    public String getQueueName() {
        return StringObjectUtils.isValid(this.queueName) ? this.queueName : null;
    }

    public String getUserName() {
        return StringObjectUtils.isValid(this.userName) ? this.userName : null;
    }

    public String getPassword() {
        return StringObjectUtils.isValid(this.password) ? this.password : null;
    }

    public String getIsAsync() {
        return StringObjectUtils.isValid(this.isAsync) ? this.isAsync : "true";
    }

    public String getIsCompression() {
        return StringObjectUtils.isValid(this.isCompression) ? this.isCompression : "true";
    }

    public String getPerformance() {
        return StringObjectUtils.isValid(this.performance) ? this.performance : "0";
    }

    public String getSamplingRate() {
        return StringObjectUtils.isValid(this.samplingRate) ? this.samplingRate : "100";
    }

    public String getParentModuleName() {
        return StringObjectUtils.isValid(this.parentModuleName) ? this.parentModuleName : "unknown";
    }
}
