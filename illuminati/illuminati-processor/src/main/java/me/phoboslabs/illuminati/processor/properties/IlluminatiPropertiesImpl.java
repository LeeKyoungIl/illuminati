/*
 * Copyright 2017 Phoboslabs.me
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package me.phoboslabs.illuminati.processor.properties;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;
import me.phoboslabs.illuminati.common.properties.IlluminatiBaseProperties;
import me.phoboslabs.illuminati.common.util.StringObjectUtils;

/**
 * Created by leekyoungil (leekyoungil@gmail.com) on 04/16/2018.
 * <p>
 * Sample - broker: rabbitmq - clusterList: pi.leekyoungil.com:5672 - virtualHost: illuminatiPiDev - topic:
 * pi-dev-illuminati-exchange - queueName: pi-dev-illuminati-exchange.illuminati - userName: illuminati-dev - password:
 * yourpassword - isAsync: true - isCompression: true - compressionType: zstd - parentModuleName: apisample - samplingRate: 100 -
 * performance: 0 // it's only using when you choose kafka.
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
    private String compressionType;

    public IlluminatiPropertiesImpl() {
        super();
    }

    public IlluminatiPropertiesImpl(Properties prop) {
        super(prop);
    }

    public String getBroker() {
        return StringObjectUtils.isValid(this.broker) ? this.broker.toLowerCase() : null;
    }

    public String getClusterList() {
        return StringObjectUtils.isValid(this.clusterList) ? this.clusterList : null;
    }

    public List<String> getClusterArrayList() throws Exception {
        if (StringObjectUtils.isValid(this.clusterList)) {
            final String[] splitedClusterList = this.clusterList.split(",");
            return Arrays.stream(splitedClusterList).map(String::trim).collect(Collectors.toList());
        } else {
            throw new Exception("clusterList is empty.");
        }
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
        return StringObjectUtils.isValid(this.performance) ? this.performance : "1";
    }

    public String getCompressionType() {
        return StringObjectUtils.isValid(this.compressionType)
            && "true".equalsIgnoreCase(this.getIsCompression()) ? this.compressionType : "none";
    }

    public String getSamplingRate() {
        return StringObjectUtils.isValid(this.samplingRate) ? this.samplingRate : "20";
    }

    public String getParentModuleName() {
        return StringObjectUtils.isValid(this.parentModuleName) ? this.parentModuleName : "unknown";
    }
}
