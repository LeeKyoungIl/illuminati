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

package me.phoboslabs.illuminati.elasticsearch.infra.param.mapping;

import java.util.HashMap;
import java.util.Map;
import me.phoboslabs.illuminati.common.util.StringObjectUtils;

public class EsIndexMapping {

    private final Map<String, Map<String, Object>> rootMappingIndex = new HashMap<>();
    private final Map<String, Object> mappingIndex = new HashMap<>();

    private final static String TYPE_KEY = "type";
    private final static String FIELD_DATE_KEY = "fielddata";
    private final static String PROPERTIES_KEY = "properties";

    public EsIndexMapping() {
    }

    public void setMappingIndexByField(String rootField, String field, String type) {
        rootField = StringObjectUtils.convertFirstLetterToLowerlize(rootField);
        Map<String, Object> fieldTypeMap = new HashMap<>();
        fieldTypeMap.put(TYPE_KEY, type);
        if ("text".equalsIgnoreCase(type)) {
            fieldTypeMap.put(FIELD_DATE_KEY, true);
        }

        Map<String, Object> targetMap = null;
        if (this.rootMappingIndex.containsKey(rootField)) {
            targetMap = this.rootMappingIndex.get(rootField);
            Map<String, Object> fieldMap = (Map<String, Object>) targetMap.get(PROPERTIES_KEY);
            fieldMap.put(field, fieldTypeMap);
            targetMap.put(PROPERTIES_KEY, fieldMap);
        } else {
            Map<String, Object> fieldMap = new HashMap<>();
            fieldMap.put(field, fieldTypeMap);

            targetMap = new HashMap<>();
            targetMap.put(PROPERTIES_KEY, fieldMap);
        }

        this.rootMappingIndex.put(rootField, targetMap);
    }

    public Map<String, Map<String, Object>> getMappingIndex() {
        return this.rootMappingIndex;
    }
}
