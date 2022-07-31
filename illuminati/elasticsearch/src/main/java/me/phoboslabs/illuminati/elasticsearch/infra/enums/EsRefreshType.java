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
 * https://www.elastic.co/guide/en/elasticsearch/reference/5.3/docs-refresh.html#docs-refresh
 */
public enum EsRefreshType {
    FALSE("false"),         // ?refresh=false ( elastic search default)
    WAIT_FOR("wait_for"),   // ?refresh=wait_for
    TRUE("true");            // ?refresh=true

    String value;

    EsRefreshType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
