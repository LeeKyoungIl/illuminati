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

import me.phoboslabs.illuminati.common.dto.enums.MappingType;

import java.util.HashMap;
import java.util.Map;

public class EsIndexMappingBuilder {

    private final EsIndexMapping esIndexMapping;
    private String esDataType = "log";

    private final static String PROPERTIES_KEY = "properties";
    private final static String MAPPINGS_KEY = "mappings";

    private EsIndexMappingBuilder () {
        this.esIndexMapping = new EsIndexMapping();
    }

    public static EsIndexMappingBuilder Builder () {
        return new EsIndexMappingBuilder();
    }

    public EsIndexMappingBuilder setEsDataType (String esDataType) {
        this.esDataType = esDataType;
        return this;
    }

    public EsIndexMappingBuilder setMapping (String rootField, String field, MappingType mappingType) {
        this.esIndexMapping.setMappingIndexByField(rootField, field, mappingType.getMappingType());
        return this;
    }

    public Map<String, Object> build () {
        Map<String, Object> propertiesMap = new HashMap<>();
        propertiesMap.put(PROPERTIES_KEY, this.esIndexMapping.getMappingIndex());

        Map<String, Object> typeMap = new HashMap<>();
        typeMap.put(this.esDataType, propertiesMap);

        Map<String, Object> mappingResultMap = new HashMap<>();
        mappingResultMap.put(MAPPINGS_KEY, typeMap);

        return mappingResultMap;
    }
}
