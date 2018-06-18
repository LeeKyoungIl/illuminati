package com.leekyoungil.illuminati.elasticsearch.infra.param.mapping;

import com.leekyoungil.illuminati.common.dto.enums.MappingType;
import com.leekyoungil.illuminati.elasticsearch.infra.EsDocument;

import java.util.HashMap;
import java.util.Map;

public class EsIndexMappingBuilder {

    private final EsIndexMapping esIndexMapping;
    private String esDataType = "log";

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
        Map<String, Object> propertiesMap = new HashMap<String, Object>();
        propertiesMap.put("properties", this.esIndexMapping.getMappingIndex());

        Map<String, Object> typeMap = new HashMap<String, Object>();
        typeMap.put(this.esDataType, propertiesMap);

        Map<String, Object> mappingResultMap = new HashMap<String, Object>();
        mappingResultMap.put("mappings", typeMap);

        return mappingResultMap;
    }
}
