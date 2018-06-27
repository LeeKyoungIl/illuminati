package com.leekyoungil.illuminati.gatekeeper.api.service

import com.leekyoungil.illuminati.common.http.IlluminatiHttpClient
import com.leekyoungil.illuminati.elasticsearch.infra.ESclientImpl
import com.leekyoungil.illuminati.elasticsearch.infra.EsClient
import com.leekyoungil.illuminati.elasticsearch.infra.enums.EsOrderType
import spock.lang.Specification

import java.util.regex.Matcher
import java.util.regex.Pattern

class JvmInfoApiServiceTest extends Specification {

    private final String elasticSearchHost = "pi.leekyoungil.com";
    private final int elasticSearchPort = 9200;

    def "get Jvm Info From Elasticsearch" () {
        setup:
        EsClient esClient = new ESclientImpl(new IlluminatiHttpClient(), this.elasticSearchHost, this.elasticSearchPort);
        esClient.setOptionalIndex("sample-illuminati*");
        JvmInfoApiService jvmInfoApiService = new JvmInfoApiService(esClient);
        List<Map<String, Object>> jvmInfo;

        when:
        jvmInfo = jvmInfoApiService.getJvmInfoFromElasticsearch();

        then:
        jvmInfo != null;
        jvmInfo.size() > 0;
    }

    def "get Jvm Info timestamp to datetime" () {
        setup:
        EsClient esClient = new ESclientImpl(new IlluminatiHttpClient(), this.elasticSearchHost, this.elasticSearchPort);
        esClient.setOptionalIndex("sample-illuminati*");
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

    def "get Jvm Info with range From Elasticsearch" () {
        setup:
        Map<String, Object> param = new HashMap<>();
        param.put("size", 3);
        EsClient esClient = new ESclientImpl(new IlluminatiHttpClient(), this.elasticSearchHost, this.elasticSearchPort);
        esClient.setOptionalIndex("sample-illuminati*");
        JvmInfoApiService jvmInfoApiService = new JvmInfoApiService(esClient);
        List<Map<String, Object>> jvmInfo;

        when:
        jvmInfo = jvmInfoApiService.getJvmInfoByConditionFromElasticsearch(param);

        then:
        jvmInfo != null;
        jvmInfo.size() == 3;
    }

    def "get Jvm Info with range by order by From Elasticsearch" () {
        setup:
        Map<String, Object> sort = new HashMap<>();
        sort.put("logTime", EsOrderType.DESC);
        Map<String, Object> param = new HashMap<>();
        param.put("from", 0);
        param.put("size", 1);
        param.put("sort", sort);
        EsClient esClient = new ESclientImpl(new IlluminatiHttpClient(), this.elasticSearchHost, this.elasticSearchPort);
        esClient.setOptionalIndex("sample-illuminati*");
        JvmInfoApiService jvmInfoApiService = new JvmInfoApiService(esClient);
        List<Map<String, Object>> jvmInfo;

        when:
        jvmInfo = jvmInfoApiService.getJvmInfoByConditionFromElasticsearch(param);

        then:
        jvmInfo != null;
        jvmInfo.size() == 1;
    }

    def "get Jvm Info by hostName From Elasticsearch" () {
        setup:
        Map<String, Object> match = new HashMap<>();
        match.put("hostName", "leekyoungils");
        Map<String, Object> sort = new HashMap<>();
        sort.put("logTime", EsOrderType.DESC);
        Map<String, Object> param = new HashMap<>();
        param.put("from", 0);
        param.put("size", 1);
        param.put("sort", sort);
        param.put("match", match)
        EsClient esClient = new ESclientImpl(new IlluminatiHttpClient(), this.elasticSearchHost, this.elasticSearchPort);
        esClient.setOptionalIndex("sample-illuminati*");
        JvmInfoApiService jvmInfoApiService = new JvmInfoApiService(esClient);
        List<Map<String, Object>> jvmInfo;

        when:
        jvmInfo = jvmInfoApiService.getJvmInfoByConditionFromElasticsearch(param);

        then:
        jvmInfo != null;
        jvmInfo.size() == 1;
    }
}
