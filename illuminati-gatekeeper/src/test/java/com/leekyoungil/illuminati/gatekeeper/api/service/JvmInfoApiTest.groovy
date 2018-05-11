package com.leekyoungil.illuminati.gatekeeper.api.service

import com.leekyoungil.illuminati.common.http.IlluminatiHttpClient
import com.leekyoungil.illuminati.elasticsearch.infra.ESclientImpl
import com.leekyoungil.illuminati.elasticsearch.infra.EsClient
import spock.lang.Specification

import java.util.regex.Matcher
import java.util.regex.Pattern

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

    def "get Jvm Info timestamp to datetime" () {
        setup:
        EsClient esClient = new ESclientImpl(new IlluminatiHttpClient(), this.elasticSearchHost, this.elasticSearchPort);
        JvmInfoApiService jvmInfoApiService = new JvmInfoApiService(esClient);
        List<Map<String, Object>> jvmInfo;
        Map<String, Object> firstJvmInfo;

        when:
        jvmInfo = jvmInfoApiService.getJvmInfoFromElasticsearch();
        firstJvmInfo = jvmInfo.get(0).get("source");

        then:
        firstJvmInfo != null;

        Pattern pattern = Pattern.compile("([0-9]{4}-[0-9]{2}-[0-9]{2} [0-9]{2}:[0-9]{2}:[0-9]{2})");
        Matcher matcher = pattern.matcher(String.valueOf(firstJvmInfo.get("timestamp")));
        matcher.find() == true;
    }
}
