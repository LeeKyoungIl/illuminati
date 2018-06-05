package com.leekyoungil.illuminati.gatekeeper.api.service;

import com.leekyoungil.illuminati.elasticsearch.infra.EsClient;
import com.leekyoungil.illuminati.elasticsearch.infra.enums.EsQueryType;
import com.leekyoungil.illuminati.elasticsearch.infra.param.RequestEsParam;
import com.leekyoungil.illuminati.elasticsearch.infra.param.query.EsQueryBuilder;
import com.leekyoungil.illuminati.elasticsearch.infra.param.sort.EsSortBuilder;
import com.leekyoungil.illuminati.elasticsearch.infra.param.source.EsSource;
import com.leekyoungil.illuminati.elasticsearch.infra.param.source.EsSourceBuilder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JvmInfoApiService extends BasicElasticsearchService {

    private static List<String> JVM_FIELD_LIST = new ArrayList<>();

    static {
        JVM_FIELD_LIST.add("jvmInfo");
        JVM_FIELD_LIST.add("timestamp");
    }

    public JvmInfoApiService (EsClient eSclient) {
        super(eSclient);
    }

//    public List<Map<String, Object>> getJvmInfoByConditionFromElasticsearch(Map<String, Object> param) {
//        Map<String, Object> requestParam = new HashMap<>(JVM_ES_FIELD_PARAM);
//        requestParam.putAll(param);
//
//        return this.requestToElasticsearch(requestParam);
//    }

    public List<Map<String, Object>> getJvmInfoFromElasticsearch() {
        return this.requestToElasticsearch(this.generateQueryForEs());
    }

//    private Map<String, Object> generateQueryForEs (Map<String, Object> param) {
//        EsQueryBuilder esQueryBuilder = EsQueryBuilder.Builder();
//        if (param.containsKey()) {
//
//        }
//    }

    private String generateQueryForEs () {
        Map<String, Object> query = EsQueryBuilder.Builder(EsQueryType.MATCH_ALL)
                                    .build();
        List<String> source = EsSourceBuilder.Builder()
                                .setSource(JVM_FIELD_LIST)
                                .builder();
        String jsonQuery = RequestEsParam.Builder(query, source)
                            .build();

        return jsonQuery;
    }
}
