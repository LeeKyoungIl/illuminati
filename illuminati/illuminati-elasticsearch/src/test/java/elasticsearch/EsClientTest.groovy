package elasticsearch

import com.google.gson.stream.JsonReader
import me.phoboslabs.illuminati.common.constant.IlluminatiConstant
import me.phoboslabs.illuminati.common.dto.enums.MappingType
import me.phoboslabs.illuminati.common.http.IlluminatiHttpClient
import me.phoboslabs.illuminati.elasticsearch.infra.ESclientImpl
import me.phoboslabs.illuminati.elasticsearch.infra.EsClient
import me.phoboslabs.illuminati.elasticsearch.infra.EsDocument
import me.phoboslabs.illuminati.elasticsearch.infra.IlluminatiEsConnector
import me.phoboslabs.illuminati.elasticsearch.infra.enums.*
import me.phoboslabs.illuminati.elasticsearch.infra.model.EsData
import me.phoboslabs.illuminati.elasticsearch.infra.model.EsDataImpl
import me.phoboslabs.illuminati.elasticsearch.infra.param.RequestEsParam
import me.phoboslabs.illuminati.elasticsearch.infra.param.groupby.EsGroupByBuilder
import me.phoboslabs.illuminati.elasticsearch.infra.param.mapping.EsIndexMappingBuilder
import me.phoboslabs.illuminati.elasticsearch.infra.param.query.EsQuery
import me.phoboslabs.illuminati.elasticsearch.infra.param.query.EsQueryBuilder
import me.phoboslabs.illuminati.elasticsearch.infra.param.sort.EsSortBuilder
import me.phoboslabs.illuminati.elasticsearch.infra.param.source.EsSourceBuilder
import me.phoboslabs.illuminati.elasticsearch.model.IlluminatiEsTemplateInterfaceModelImpl
import spock.lang.Specification

import java.nio.file.Files
import java.nio.file.Paths

class EsClientTest extends Specification {

    def setup() {
        System.setProperty("spring.profiles.active", "test")
        IlluminatiEsConnector illuminatiEsConnector = new IlluminatiEsConnector()

        EsClient esClient = new ESclientImpl(new IlluminatiHttpClient(), illuminatiEsConnector.getHost(), illuminatiEsConnector.getPort())
//        esClient.setOptionalIndex("sample-illuminati-2021.11.28")

        JsonReader reader = new JsonReader(new FileReader("src/test/resources/sample/sample1.json"));
        TestEsTemplateInterfaceModelImpl sampleEsTemplateInterfaceModel = IlluminatiConstant.ILLUMINATI_GSON_OBJ.fromJson(reader, TestEsTemplateInterfaceModelImpl.class);

        esClient.save(sampleEsTemplateInterfaceModel)
    }

    def cleanup() {
    }

    def "get all value in a field from elasticsearch"() {
        setup:
        Map<String, Object> esQuery = EsQueryBuilder.Builder()
                .setMatchAll()
                .build();


        List<String> esSource = EsSourceBuilder.Builder()
                .setSource("jvmInfo")
                .setSource("timestamp")
                .build();

        String queryString = RequestEsParam.Builder(esQuery, esSource)
                .build()

        System.setProperty("spring.profiles.active", "test");
        IlluminatiEsConnector illuminatiEsConnector = new IlluminatiEsConnector();

        EsClient esClient = new ESclientImpl(new IlluminatiHttpClient(), illuminatiEsConnector.getHost(), illuminatiEsConnector.getPort());
//        esClient.setOptionalIndex("sample-illuminati*");

        when:
        String data = esClient.getDataByJson(queryString);

        then:
        data != null;
        data.indexOf("error") == -1
    }

    def "make jvm data from source data"() {
        setup:
        Map<String, Object> esQuery = EsQueryBuilder.Builder()
                .setMatchAll()
                .build();

        List<String> esSource = EsSourceBuilder.Builder()
                .setSource("jvmInfo")
                .setSource("timestamp")
                .build();

        String queryString = RequestEsParam.Builder(esQuery, esSource)
                .setSort(EsSortBuilder.Builder().build())
                .build()

        System.setProperty("spring.profiles.active", "test");
        IlluminatiEsConnector illuminatiEsConnector = new IlluminatiEsConnector();

        EsClient esClient = new ESclientImpl(new IlluminatiHttpClient(), illuminatiEsConnector.getHost(), illuminatiEsConnector.getPort());
        esClient.setOptionalIndex("sample-illuminati*");
        String data = esClient.getDataByJson(queryString);

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

    def "jvm data by range"() {
        setup:
        List<String> esSource = EsSourceBuilder.Builder()
                .setSource("jvmInfo")
                .setSource("timestamp")
                .build();

        Map<String, Object> esQuery = EsQueryBuilder.Builder()
                .setMatchAll()
                .setRange(EsRangeType.GTE, "2021-12-05T00:00:00")
                .setRange(EsRangeType.LTE, "2021-12-05T23:59:59")
                .build();

        Map<String, String> sort = EsSortBuilder.Builder()
                .setSort(EsOrderType.DESC, "logTime")
                .build();

        String queryString = RequestEsParam.Builder(esQuery, esSource)
                .setSort(sort)
                .setSize(20)
                .setFrom(0)
                .build()

        System.setProperty("spring.profiles.active", "test");
        IlluminatiEsConnector illuminatiEsConnector = new IlluminatiEsConnector();

        EsClient esClient = new ESclientImpl(new IlluminatiHttpClient(), illuminatiEsConnector.getHost(), illuminatiEsConnector.getPort());
        esClient.setOptionalIndex("sample-illuminati*");
        String data = esClient.getDataByJson(queryString);

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

    def "get host bt group by"() {
        setup:
        Map<String, Object> esQuery = EsQueryBuilder.Builder()
                .setMatchAll()
                .build();

        Map<String, Object> esGroupBy = EsGroupByBuilder.Builder()
                .setGroupByKey("serverInfo.hostName")
                .setGroupByKey("serverInfo.serverIp")
                .build();

        String queryString = new RequestEsParam(esQuery)
                .setGroupBy(esGroupBy)
                .build();

        System.setProperty("spring.profiles.active", "test");
        IlluminatiEsConnector illuminatiEsConnector = new IlluminatiEsConnector();

        EsClient esClient = new ESclientImpl(new IlluminatiHttpClient(), illuminatiEsConnector.getHost(), illuminatiEsConnector.getPort());
        esClient.setOptionalIndex("sample-illuminati*");
        String data = esClient.getDataByJson(queryString);

        when:

        EsData esData = new EsDataImpl(data);
        List<Map<String, Object>> resultList = esData.getEsDataList();

        then:
        resultList.size() > 0
        resultList.each { map ->
            map.containsKey("key") == true;
        }
    }

    def "get host bt group by single"() {
        setup:
        Map<String, Object> esQuery = EsQueryBuilder.Builder()
                .setMatchAll()
                .build();

        Map<String, Object> esGroupBy = EsGroupByBuilder.Builder()
                .setGroupByKey("serverInfo.hostName")
                .build();

        String queryString = new RequestEsParam(esQuery)
                .setGroupBy(esGroupBy)
                .build();

        System.setProperty("spring.profiles.active", "test");
        IlluminatiEsConnector illuminatiEsConnector = new IlluminatiEsConnector();

        EsClient esClient = new ESclientImpl(new IlluminatiHttpClient(), illuminatiEsConnector.getHost(), illuminatiEsConnector.getPort());
        esClient.setOptionalIndex("sample-illuminati*");
        String data = esClient.getDataByJson(queryString);

        when:

        EsData esData = new EsDataImpl(data);
        List<Map<String, Object>> resultList = esData.getEsDataList();

        then:
        resultList.size() > 0
        resultList.each { map ->
            map.containsKey("key") == true;
        }
    }

    def "query builder test"() {
        setup:
        EsQuery esQuery;

        when:
        esQuery = EsQueryBuilder.Builder()
                .setQueryType(EsQueryType.MATCH_ALL)
                .build();

        then:
        esQuery != null;
    }

    def "es request builder"() {
        setup:
        RequestEsParam requestEsParam;
        List<String> requestEsSourceParam;
        boolean result = false;

        when:
        requestEsSourceParam = EsSourceBuilder.Builder()
                .setSource("timestamp")
                .setSource("jvmInfo")
                .build();

        Map<String, String> esSort = new EsSortBuilder()
                .setSort(EsOrderType.DESC, "logTime")
                .build();

        Map<String, Object> esQuery = EsQueryBuilder.Builder()
                .setQueryType(EsQueryType.MATCH_ALL)
                .build();

        requestEsParam = RequestEsParam.Builder(esQuery, requestEsSourceParam);
        String jsonString = requestEsParam
                .setSort(esSort)
                .build();

        System.setProperty("spring.profiles.active", "test");
        IlluminatiEsConnector illuminatiEsConnector = new IlluminatiEsConnector();

        EsClient esClient = new ESclientImpl(new IlluminatiHttpClient(), illuminatiEsConnector.getHost(), illuminatiEsConnector.getPort());
        esClient.setOptionalIndex("sample-illuminati*");
        String data = esClient.getDataByJson(jsonString);

        EsData esData = new EsDataImpl(data);
        List<Map<String, Object>> resultList = esData.getEsDataList();

        then:
        resultList != null
        resultList.size() > 0
        resultList.each { map ->
            Map<String, Object> resultMap = map.get("source");
            if (resultMap.size() == 2) {
                result = true;
            } else {
                result = false;
            }
        }

        result == true
    }

    def "get data by search"() {
        setup:
        boolean checkResult = true;

        List<String> esSource = EsSourceBuilder.Builder()
                .setSource("timestamp")
                .setSource("jvmInfo")
                .setSource("serverInfo")
                .build();

        Map<String, String> esSort = new EsSortBuilder()
                .setSort(EsOrderType.DESC, "logTime")
                .build();

        Map<String, Object> esQuery = EsQueryBuilder.Builder()
                .setQueryType(EsQueryType.MATCH)
                .setMatch("serverInfo.hostName", "leekyoungil-p1")
                .build();

        String queryString = new RequestEsParam(esQuery, esSource)
                .setSource(esSource)
                .setSort(esSort)
                .build();

        System.setProperty("spring.profiles.active", "test");
        IlluminatiEsConnector illuminatiEsConnector = new IlluminatiEsConnector();

        EsClient esClient = new ESclientImpl(new IlluminatiHttpClient(), illuminatiEsConnector.getHost(), illuminatiEsConnector.getPort());
        esClient.setOptionalIndex("sample-illuminati*");
        String data = esClient.getDataByJson(queryString);

        when:

        EsData esData = new EsDataImpl(data);
        List<Map<String, Object>> resultList = esData.getEsDataList();

        then:
        resultList.size() > 0
        resultList.each { map ->
            Map<String, Object> resultMap = map.get("source");
            Map<String, Object> resultMap2 = resultMap.get("serverInfo");
            if ("leekyoungil-p1".equals(resultMap2.get("hostName")) == true) {
                checkResult = true;
            } else {
                checkResult = false
            }
        }
        checkResult == true;
    }

    def "get mapping with builder"() {
        setup:
        EsIndexMappingBuilder esIndexMappingBuilder = EsIndexMappingBuilder.Builder();

        when:
        esIndexMappingBuilder.setMapping("serverInfo", "hostName", MappingType.KEYWORD);
        esIndexMappingBuilder.setMapping("serverInfo", "serverIp", MappingType.KEYWORD);
        Map<String, Map<String, Object>> indexMappingJson = esIndexMappingBuilder.build();

        then:
        indexMappingJson != null;
    }

    def "get mapping struct from model"() {
        setup:
        String jsonString = Files.readString(Paths.get("src/test/resources/sample/sample1.json"));
        TestEsTemplateInterfaceModelImpl sampleEsTemplateInterfaceModel = IlluminatiConstant.ILLUMINATI_GSON_OBJ.fromJson(jsonString, TestEsTemplateInterfaceModelImpl.class);
        String mappingJsonString;


        when:
        mappingJsonString = sampleEsTemplateInterfaceModel.getIndexMapping();

        then:
        mappingJsonString != null;
    }
}

@EsDocument(indexName = "sample-illuminati", type = "log", indexStoreType = EsIndexStoreType.FS, shards = 1, replicas = 0, refreshType = EsRefreshType.TRUE)
public class TestEsTemplateInterfaceModelImpl extends IlluminatiEsTemplateInterfaceModelImpl {

    public TestEsTemplateInterfaceModelImpl() {
        super();
    }
}
