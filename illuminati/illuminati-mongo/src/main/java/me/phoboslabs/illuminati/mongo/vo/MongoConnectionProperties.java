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

package me.phoboslabs.illuminati.mongo.vo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MongoConnectionProperties {

    private final static Logger MONGO_LOGGER = LoggerFactory.getLogger(MongoConnectionProperties.class);

    private MongoInfo mongoInfo;

    public void setMongoInfo(MongoInfo mongoInfo) {
        this.mongoInfo = mongoInfo;
    }

    public MongoInfo getMongoInfo() {
        return this.mongoInfo;
    }

    public String getMongoConnectionURI() {
        if (this.mongoInfo == null) {
            throw new IllegalArgumentException("mongo info config file must not be null.");
        }
        return new StringBuilder().append("mongodb://").append(this.mongoInfo.hostAddress)
                .append(":").append(this.mongoInfo.port).toString();
    }

    public static class MongoInfo {
        private String hostAddress;
        private int port;

        public void setHostAddress(String hostAddress) {
            this.hostAddress = hostAddress;
        }

        public void setPort(int port) {
            this.port = port;
        }

        public String getHostAddress() {
            return this.hostAddress;
        }

        public int getPort() {
            return this.port;
        }
    }
}
