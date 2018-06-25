package com.leekyoungil.illuminati.gatekeeper.api.service;

import com.leekyoungil.illuminati.common.util.ConvertUtil;
import com.leekyoungil.illuminati.elasticsearch.infra.EsClient;
import com.leekyoungil.illuminati.elasticsearch.infra.enums.EsOrderType;
import com.leekyoungil.illuminati.elasticsearch.infra.enums.EsQueryType;
import com.leekyoungil.illuminati.elasticsearch.infra.param.RequestEsParam;
import com.leekyoungil.illuminati.elasticsearch.infra.param.query.EsQueryBuilder;
import com.leekyoungil.illuminati.elasticsearch.infra.param.sort.EsSortBuilder;
import com.leekyoungil.illuminati.elasticsearch.infra.param.source.EsSourceBuilder;

import java.util.ArrayList;
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

    public List<Map<String, Object>> getJvmInfoByConditionFromElasticsearch(Map<String, Object> param) {
        return this.requestToElasticsearch(this.generateQueryForEsWithParam(param));
    }

    public List<Map<String, Object>> getJvmInfoFromElasticsearch() {
        return this.requestToElasticsearch(this.generateQueryForEs());
    }

    private String generateQueryForEs () {
        return this.generateQueryForEs(this.getRequestEsParam(), null);
    }

    private String generateQueryForEsWithParam (Map<String, Object> param) {
        return this.generateQueryForEs(this.getRequestEsParam(), param);
    }

    private RequestEsParam getRequestEsParam () {
        Map<String, Object> query = EsQueryBuilder.Builder(EsQueryType.MATCH_ALL)
                .build();
        List<String> source = EsSourceBuilder.Builder()
                .setSource(JVM_FIELD_LIST)
                .build();

        return RequestEsParam.Builder(query, source);
    }

    private String generateQueryForEs (RequestEsParam requestEsParam, Map<String, Object> param) {
        if (param != null && param.size() > 0) {
            final String sizeKey = "size", fromKey = "from", sortKey = "sort", matchKey = "match";
            if (param.containsKey(sizeKey)) {
                requestEsParam.setSize((int)param.get(sizeKey));
            }
            if (param.containsKey(fromKey)) {
                requestEsParam.setFrom((int)param.get(fromKey));
            }
            if (param.containsKey(sortKey)) {
                final EsSortBuilder esSortBuilder = EsSortBuilder.Builder();
                ConvertUtil.castToMapOf(String.class, Object.class, Map.class.cast(param.get(sortKey))).forEach((k, v) -> {
                    esSortBuilder.setSort(EsOrderType.class.cast(v), k);
                });

                requestEsParam.setSort(esSortBuilder.build());
            }
            if (param.containsKey(matchKey)) {
                final EsQueryBuilder esQueryBuilder = EsQueryBuilder.Builder();
                Map<String, Object> match = ConvertUtil.castToMapOf(String.class, Object.class, Map.class.cast(param.get(matchKey)));
                if (match != null && match.size() > 0) {
                    esQueryBuilder.setQueryType(EsQueryType.MATCH);
                    match.forEach((k, v) -> {
                        esQueryBuilder.setMatch(k, v);
                    });
                } else {
                    esQueryBuilder.setQueryType(EsQueryType.MATCH_ALL);
                }
                requestEsParam.setQuery(esQueryBuilder.build());
            }
        }

        return requestEsParam.build();
    }
}
