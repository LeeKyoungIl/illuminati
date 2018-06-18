package com.leekyoungil.illuminati.elasticsearch.infra.param.mapping;

import java.util.HashMap;
import java.util.Map;

public class EsIndexMapping {

    private final Map<String, Map<String, Object>> rootMappingIndex = new HashMap<String, Map<String, Object>>();
    private final Map<String, Object> mappingIndex = new HashMap<String, Object>();

    public EsIndexMapping () {
    }

    public void setMappingIndexByField (String rootField, String field, String type) {
        Map<String, String> fieldTypeMap = new HashMap<String, String>();
        fieldTypeMap.put("type", type);

        Map<String, Object> targetMap = null;
        if (this.rootMappingIndex.containsKey(rootField) == true) {
            targetMap = this.rootMappingIndex.get(rootField);
            Map<String, Object> fieldMap = (Map<String, Object>) targetMap.get("properties");
            fieldMap.put(field, fieldTypeMap);
            targetMap.put("properties", fieldMap);
        } else {
            Map<String, Object> fieldMap = new HashMap<String, Object>();
            fieldMap.put(field, fieldTypeMap);

            targetMap = new HashMap<String, Object>();
            targetMap.put("properties", fieldMap);
        }

        this.rootMappingIndex.put(rootField, targetMap);
    }

    public Map<String, Map<String, Object>> getMappingIndex () {
        return this.rootMappingIndex;
    }
}
