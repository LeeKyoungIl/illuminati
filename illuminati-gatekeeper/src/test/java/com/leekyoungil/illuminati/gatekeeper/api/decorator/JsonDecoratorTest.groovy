package com.leekyoungil.illuminati.gatekeeper.api.decorator

import com.leekyoungil.illuminati.common.dto.IlluminatiRestApiResult
import com.leekyoungil.illuminati.common.http.IlluminatiHttpClient
import com.leekyoungil.illuminati.elasticsearch.infra.ESclientImpl
import com.leekyoungil.illuminati.elasticsearch.infra.EsClient
import com.leekyoungil.illuminati.gatekeeper.api.service.JvmInfoApiService
import spock.lang.Specification

class JsonDecoratorTest extends Specification {

    private final String elasticSearchHost = "pi.leekyoungil.com";
    private final int elasticSearchPort = 9200;

    def "get Jvm Info From Elasticsearch by JSON API Decorator" () {
        setup:
        EsClient esClient = new ESclientImpl(new IlluminatiHttpClient(), this.elasticSearchHost, this.elasticSearchPort);
        JvmInfoApiService jvmInfoApiService = new JvmInfoApiService(esClient);
        List<Map<String, Object>> jvmInfo;
        IlluminatiRestApiResult<List<Map<String, Object>>> illuminatiJsonResult = null;

        when:
        jvmInfo = jvmInfoApiService.getJvmInfoFromElasticsearch();
        ApiDecorator apiJsonDecorator = new ApiJsonDecorator(jvmInfo);
        illuminatiJsonResult = apiJsonDecorator.getIlluminatiTypeObject();

        then:
        illuminatiJsonResult != null
        illuminatiJsonResult.getCode() == 200
    }
}
