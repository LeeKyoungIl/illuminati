package com.leekyoungil.illuminati.gatekeeper.api.service;

import com.leekyoungil.illuminati.elasticsearch.infra.EsClient;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JvmInfoApiService extends BasicElasticsearchService {

    private final static Map<String, Object> JVM_ES_FIELD_PARAM = new HashMap<>();
    static {
        List<String> jvmFieldList = new ArrayList<>();
        jvmFieldList.add("jvmInfo");
        jvmFieldList.add("timestamp");
        JVM_ES_FIELD_PARAM.put("source", jvmFieldList);
    }

    public JvmInfoApiService (EsClient eSclient) {
        super(eSclient);
    }

    public List<Map<String, Object>> getJvmInfoByConditionFromElasticsearch(Map<String, Object> param) {
        Map<String, Object> requestParam = new HashMap<>(JVM_ES_FIELD_PARAM);
        requestParam.putAll(param);

        return this.requestToElasticsearch(requestParam);
    }

    public List<Map<String, Object>> getJvmInfoFromElasticsearch() {
        return this.requestToElasticsearch(JVM_ES_FIELD_PARAM);
    }
}
