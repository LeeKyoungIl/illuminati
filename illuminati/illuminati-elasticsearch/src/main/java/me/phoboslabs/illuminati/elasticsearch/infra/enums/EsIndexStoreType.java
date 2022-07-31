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

package me.phoboslabs.illuminati.elasticsearch.infra.enums;

/**
 * https://www.elastic.co/guide/en/elasticsearch/reference/current/index-modules-store.html
 */
public enum EsIndexStoreType {

    FS("fs"),
    SIMPLEFS("simplefs"),
    NIOFS("niofs"),
    MMAPFS("mmapfs");

    private final String type;

    EsIndexStoreType(String type) {
        this.type = type;
    }

    public String getType() {
        return this.type;
    }

    public static EsIndexStoreType getEnumType(String type) throws Exception {
        switch (type) {
            case "fs":
                return FS;
            case "simplefs":
                return SIMPLEFS;
            case "niofs":
                return NIOFS;
            case "mmapfs":
                return MMAPFS;
            default:
                throw new Exception("check type value.");
        }
    }
}
