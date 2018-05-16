package elasticsearch

import com.leekyoungil.illuminati.common.http.IlluminatiHttpClient
import com.leekyoungil.illuminati.elasticsearch.infra.ESclientImpl
import com.leekyoungil.illuminati.elasticsearch.infra.EsClient
import com.leekyoungil.illuminati.elasticsearch.infra.model.EsData
import com.leekyoungil.illuminati.elasticsearch.infra.model.EsDataImpl
import spock.lang.Specification

class EsClientTest extends Specification {

    private final String elasticSearchHost = "pi.leekyoungil.com";
    private final int elasticSearchPort = 9200;

    def "get all value in a field from elasticsearch" () {
        setup:
        List<String> fields = new ArrayList<>();
        fields.add("jvmInfo");
        fields.add("timestamp");
        Map<String, Object> param =  new HashMap<>();
        param.put("source", fields)

        EsClient esClient = new ESclientImpl(new IlluminatiHttpClient(), this.elasticSearchHost, this.elasticSearchPort);
        esClient.setOptionalIndex("sample-illuminati*");

        when:
        String data = esClient.getDataByParam(param);

        then:
        data != null;
    }

    def "make jvm data from source data" () {
        setup:
        List<String> fields = new ArrayList<>();
        fields.add("jvmInfo");
        fields.add("timestamp");
        Map<String, Object> param =  new HashMap<>();
        param.put("source", fields)

        EsClient esClient = new ESclientImpl(new IlluminatiHttpClient(), this.elasticSearchHost, this.elasticSearchPort);
        esClient.setOptionalIndex("sample-illuminati*");
        String data = esClient.getDataByParam(param);

        when:
        EsData esData = new EsDataImpl(data);
        List<Map<String, Object>> resultList = esData.getEsDataList();

        then:
        resultList.size() > 0
        resultList.each { map ->
            map.containsKey("source") == true;
            Map<String, Object> checkMap = map.get("source");
            checkMap.containsKey("jvmInfo") == true;
        }
    }
}
