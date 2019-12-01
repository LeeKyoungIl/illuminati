package me.phoboslabs.illuminati.elasticsearch.infra.param.mapping;

import me.phoboslabs.illuminati.common.util.StringObjectUtils;

import java.util.HashMap;
import java.util.Map;

public class EsIndexMapping {

    private final Map<String, Map<String, Object>> rootMappingIndex = new HashMap<String, Map<String, Object>>();
    private final Map<String, Object> mappingIndex = new HashMap<String, Object>();

    private final static String TYPE_KEY = "type";
    private final static String PROPERTIES_KEY = "properties";

    public EsIndexMapping () {
    }

    public void setMappingIndexByField (String rootField, final String field, final String type) {
        rootField = StringObjectUtils.convertFirstLetterToLowerlize(rootField);
        Map<String, String> fieldTypeMap = new HashMap<String, String>();
        fieldTypeMap.put(TYPE_KEY, type);

        Map<String, Object> targetMap = null;
        if (this.rootMappingIndex.containsKey(rootField)) {
            targetMap = this.rootMappingIndex.get(rootField);
            Map<String, Object> fieldMap = (Map<String, Object>) targetMap.get(PROPERTIES_KEY);
            fieldMap.put(field, fieldTypeMap);
            targetMap.put(PROPERTIES_KEY, fieldMap);
        } else {
            Map<String, Object> fieldMap = new HashMap<String, Object>();
            fieldMap.put(field, fieldTypeMap);

            targetMap = new HashMap<String, Object>();
            targetMap.put(PROPERTIES_KEY, fieldMap);
        }

        this.rootMappingIndex.put(rootField, targetMap);
    }

    public Map<String, Map<String, Object>> getMappingIndex () {
        return this.rootMappingIndex;
    }
}
