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

package me.phoboslabs.illuminati.common.dto.enums;

public enum IlluminatiStorageType {

    H2("org.h2.Driver"),
    MYSQL("mysql"),
    FILE("file"),
    BROKER("broker");

    private final String type;

    IlluminatiStorageType(String type) {
        this.type = type;
    }

    public String getType() {
        return this.type;
    }

    public static IlluminatiStorageType getEnumType(String type) throws Exception {
        switch (type) {
            case "org.h2.Driver":
                return H2;
            case "mysql":
                return MYSQL;
            case "file":
                return FILE;
            case "broker":
                return BROKER;
            default:
                throw new Exception(type + " is not support yet.");
        }
    }
}
