package me.phoboslabs.illuminati.gatekeeper.api.service;

import me.phoboslabs.illuminati.elasticsearch.infra.EsClient;
import me.phoboslabs.illuminati.elasticsearch.infra.param.RequestEsParam;
import me.phoboslabs.illuminati.elasticsearch.infra.param.groupby.EsGroupByBuilder;
import me.phoboslabs.illuminati.elasticsearch.infra.param.query.EsQueryBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class HostInfoService extends BasicElasticsearchService {

    private final static List<String> GROUP_BY_COLUMNS = new ArrayList<>();

    static {
        GROUP_BY_COLUMNS.add("serverInfo.hostName");
    }

    public HostInfoService (EsClient eSclient) {
        super(eSclient);
    }

    public List<Map<String,Object>> getHostInfoFromElasticsearch() {
        Map<String, Object> query = EsQueryBuilder.Builder()
                                        .setMatchAll()
                                        .build();
        final EsGroupByBuilder groupByBuilder = EsGroupByBuilder.Builder();

        GROUP_BY_COLUMNS.forEach(v -> {
            groupByBuilder.setGroupByKey(v);
        });

        String jsonQueryRequest = RequestEsParam.Builder(query)
                                    .setGroupBy(groupByBuilder.build())
                                    .build();
        return this.requestToElasticsearch(jsonQueryRequest);
    }
}
