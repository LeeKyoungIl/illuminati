package com.leekyoungil.illuminati.gatekeeper.api.service;

import com.leekyoungil.illuminati.elasticsearch.infra.EsClient;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HostInfoService extends BasicElasticsearchService {

    private final static Map<String, Object> GROUP_BY_PARAM = new HashMap<>();

    static {
        GROUP_BY_PARAM.put("group_by", "serverInfo.hostName");
    }

    public HostInfoService (EsClient eSclient) {
        super(eSclient);
    }

//    public List<Map<String,Object>> getHostInfoFromElasticsearch() {
//        return this.requestToElasticsearch(GROUP_BY_PARAM);
//    }
}
