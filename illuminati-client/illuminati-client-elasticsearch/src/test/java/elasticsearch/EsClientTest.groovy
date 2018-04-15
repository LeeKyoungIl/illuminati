package elasticsearch

import com.leekyoungil.illuminati.common.http.IlluminatiHttpClient
import com.leekyoungil.illuminati.common.properties.IlluminatiPropertiesHelper
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
        List<String> filedNames = new ArrayList<>();
        filedNames.add("jvmInfo");
        filedNames.add("timestamp");

        EsClient esClient = new ESclientImpl(new IlluminatiHttpClient(), this.elasticSearchHost, this.elasticSearchPort);

        when:
        String data = esClient.getAllDataByFields(filedNames);

        then:
        println(data);
        data != null;
    }

    def "make jvm data from source data" () {
        setup:
        List<String> filedNames = new ArrayList<>();
        filedNames.add("jvmInfo");
        filedNames.add("timestamp");

        EsClient esClient = new ESclientImpl(new IlluminatiHttpClient(), this.elasticSearchHost, this.elasticSearchPort);
        String data = esClient.getAllDataByFields(filedNames);

        when:
        EsData esData = new EsDataImpl(data);
        List<Map<String, Object>> resultList = esData.getEsDataList();

        then:
        resultList.size() > 0
    }

    // def "get key from yml" () {
    //     setup:
    //     IlluminatiPropertiesHelper.getPropertiesValueByKey()

    //     when:

    //     then:
    // }
}
