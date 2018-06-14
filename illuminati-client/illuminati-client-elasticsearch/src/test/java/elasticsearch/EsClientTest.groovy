package elasticsearch

import com.leekyoungil.illuminati.common.http.IlluminatiHttpClient
import com.leekyoungil.illuminati.elasticsearch.infra.ESclientImpl
import com.leekyoungil.illuminati.elasticsearch.infra.EsClient
import com.leekyoungil.illuminati.elasticsearch.infra.enums.EsOrderType
import com.leekyoungil.illuminati.elasticsearch.infra.enums.EsQueryType
import com.leekyoungil.illuminati.elasticsearch.infra.enums.EsRangeType
import com.leekyoungil.illuminati.elasticsearch.infra.model.EsData
import com.leekyoungil.illuminati.elasticsearch.infra.model.EsDataImpl
import com.leekyoungil.illuminati.elasticsearch.infra.param.RequestEsParam
import com.leekyoungil.illuminati.elasticsearch.infra.param.groupby.EsGroupByBuilder
import com.leekyoungil.illuminati.elasticsearch.infra.param.source.EsSource
import com.leekyoungil.illuminati.elasticsearch.infra.param.query.EsQuery
import com.leekyoungil.illuminati.elasticsearch.infra.param.query.EsQueryBuilder
import com.leekyoungil.illuminati.elasticsearch.infra.param.sort.EsSortBuilder
import com.leekyoungil.illuminati.elasticsearch.infra.param.source.EsSourceBuilder
import spock.lang.Specification

import java.text.SimpleDateFormat

class EsClientTest extends Specification {

    private final String elasticSearchHost = "pi.leekyoungil.com";
    private final int elasticSearchPort = 9200;

    def "get all value in a field from elasticsearch" () {
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

        EsClient esClient = new ESclientImpl(new IlluminatiHttpClient(), this.elasticSearchHost, this.elasticSearchPort);
        esClient.setOptionalIndex("sample-illuminati*");

        when:
        String data = esClient.getDataByJson(queryString);

        then:
        data != null;
    }

    def "make jvm data from source data" () {
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

        EsClient esClient = new ESclientImpl(new IlluminatiHttpClient(), this.elasticSearchHost, this.elasticSearchPort);
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

    def "jvm data by range" () {
        setup:
        List<String> esSource = EsSourceBuilder.Builder()
                                .setSource("jvmInfo")
                                .setSource("timestamp")
                                .build();

        Map<String, Object> esQuery = EsQueryBuilder.Builder()
                                        .setMatchAll()
                                        .setRange(EsRangeType.GTE, "2018-03-04T00:04:20")
                                        .setRange(EsRangeType.LTE, "2018-03-05T00:06:20")
                                        .build();

        Map<String, String> sort = EsSortBuilder.Builder()
                                    .setSort(EsOrderType.DESC, "logTime")
                                    .build();

        String queryString = RequestEsParam.Builder(esQuery, esSource)
                                .setSort(sort)
                                .setSize(20)
                                .setFrom(10)
                                .build()

        EsClient esClient = new ESclientImpl(new IlluminatiHttpClient(), this.elasticSearchHost, this.elasticSearchPort);
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

    def "get host bt group by" () {
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

        EsClient esClient = new ESclientImpl(new IlluminatiHttpClient(), this.elasticSearchHost, this.elasticSearchPort);
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

    def "get host bt group by single" () {
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

        EsClient esClient = new ESclientImpl(new IlluminatiHttpClient(), this.elasticSearchHost, this.elasticSearchPort);
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

    def "query builder test" () {
        setup:
        EsQuery esQuery;

        when:
        esQuery = EsQueryBuilder.Builder()
                        .setQueryType(EsQueryType.MATCH_ALL)
                        .build();

        then:
        esQuery != null;
    }

    def "es request builder" () {
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

        EsClient esClient = new ESclientImpl(new IlluminatiHttpClient(), this.elasticSearchHost, this.elasticSearchPort);
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

    def "get data by search" () {
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
                .setMatch("serverInfo.hostName", "leekyoungils")
                .build();

        String queryString = new RequestEsParam(esQuery, esSource)
                .setSource(esSource)
                .setSort(esSort)
                .build();

        EsClient esClient = new ESclientImpl(new IlluminatiHttpClient(), this.elasticSearchHost, this.elasticSearchPort);
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
            if ("leekyoungils".equals(resultMap2.get("hostName")) == true) {
                checkResult = true;
            } else {
                checkResult = false
            }
        }
        checkResult == true;
    }
}
