package com.leekyoungil.illuminati.gatekeeper.api.service

import com.leekyoungil.illuminati.common.http.IlluminatiHttpClient
import com.leekyoungil.illuminati.elasticsearch.infra.ESclientImpl
import com.leekyoungil.illuminati.elasticsearch.infra.EsClient
import spock.lang.Specification

class JvmInfoApiTest extends Specification {

    private final String elasticSearchHost = "pi.leekyoungil.com";
    private final int elasticSearchPort = 9200;

    def "get Jvm Info From Elasticsearch" () {
        setup:
        EsClient esClient = new ESclientImpl(new IlluminatiHttpClient(), this.elasticSearchHost, this.elasticSearchPort);
        JvmInfoApiService jvmInfoApiService = new JvmInfoApiService(esClient);
        List<Map<String, Object>> jvmInfo;

        when:
        jvmInfo = jvmInfoApiService.getJvmInfoFromElasticsearch();

        then:
        jvmInfo != null;
    }
}
