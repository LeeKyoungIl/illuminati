package me.phoboslabs.illuminati.gatekeeper.api.service

import me.phoboslabs.illuminati.common.http.IlluminatiHttpClient
import me.phoboslabs.illuminati.elasticsearch.infra.ESclientImpl
import me.phoboslabs.illuminati.elasticsearch.infra.EsClient
import spock.lang.Specification

class HostInfoServiceTest extends Specification {

    private final String elasticSearchHost = "pi.leekyoungil.com";
    private final int elasticSearchPort = 9200;

    def "get host info list" () {
        setup:
        EsClient esClient = new ESclientImpl(new IlluminatiHttpClient(), this.elasticSearchHost, this.elasticSearchPort);
        esClient.setOptionalIndex("sample-illuminati*");
        HostInfoService hostInfoService = new HostInfoService(esClient);
        List<Map<String, Object>> hostInfo;

        when:
        hostInfo = hostInfoService.getHostInfoFromElasticsearch();

        then:
        hostInfo != null;
        hostInfo.size() > 0;
    }
}
