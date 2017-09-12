package com.leekyoungil.illuminati.client.prossor.config;

import com.leekyoungil.illuminati.client.prossor.util.StringUtils;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Properties;

/**
 * Created by leekyoungil (leekyoungil@gmail.com) on 06/07/2017.
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
public class IlluminatiProperties {

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

    // * it is very dangerous function. it is activate when debug is true.
    // * after using this function. you must have to re compile.(clean first)
    private String chaosBomber;

    public static boolean ILLUMINATI_DEBUG = false;

    private static final String[] propertiesKeys;

    static {
        propertiesKeys = new String[]{"parentModuleName", "samplingRate", "broker", "clusterList", "virtualHost", "topic", "queueName", "userName", "password", "isAsync"
                , "isCompression", "performance", "debug", "chaosBomber"};
    }

    public IlluminatiProperties () {}

    public IlluminatiProperties (Properties prop) {
        for (String keys : propertiesKeys) {
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

    public String getBroker() {
        return StringUtils.isValid(this.broker) ? this.broker.toLowerCase() : null;
    }

    public String getClusterList() {
        return this.clusterList;
    }

    public String getVirtualHost() {
        return this.virtualHost;
    }

    public String getTopic() {
        return StringUtils.isValid(this.topic) ? this.topic : "";
    }

    public String getQueueName() {
        return StringUtils.isValid(this.queueName) ? this.queueName : "";
    }

    public String getUserName() {
        return StringUtils.isValid(this.userName) ? this.userName : "";
    }

    public String getPassword() {
        return StringUtils.isValid(this.password) ? this.password : "";
    }

    public String getIsAsync() {
        return StringUtils.isValid(this.isAsync) ? this.isAsync : "";
    }

    public String getIsCompression() {
        return StringUtils.isValid(this.isCompression) ? this.isCompression : "";
    }

    public String getPerformance() {
        return StringUtils.isValid(this.performance) ? this.performance : "";
    }

    public String getDebug() {
        return StringUtils.isValid(this.debug) ? this.debug : "";
    }

    public String getChaosBomber() {
        return StringUtils.isValid(this.chaosBomber) ? this.chaosBomber : "";
    }

    public String getParentModuleName() {
        return StringUtils.isValid(this.parentModuleName) ? this.parentModuleName : "unknown";
    }
}
