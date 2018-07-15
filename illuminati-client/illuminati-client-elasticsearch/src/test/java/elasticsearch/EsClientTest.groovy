package elasticsearch

import me.phoboslabs.illuminati.common.constant.IlluminatiConstant
import me.phoboslabs.illuminati.common.dto.enums.MappingType
import me.phoboslabs.illuminati.common.http.IlluminatiHttpClient
import me.phoboslabs.illuminati.elasticsearch.infra.ESclientImpl
import me.phoboslabs.illuminati.elasticsearch.infra.EsClient
import me.phoboslabs.illuminati.elasticsearch.infra.EsDocument
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
        data.indexOf("error") == -1
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
                .setMatch("serverInfo.hostName", "leekyoungil-t480s")
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
            if ("leekyoungil-t480s".equals(resultMap2.get("hostName")) == true) {
                checkResult = true;
            } else {
                checkResult = false
            }
        }
        checkResult == true;
    }

    def "get mapping with builder" () {
        setup:
        EsIndexMappingBuilder esIndexMappingBuilder = EsIndexMappingBuilder.Builder();

        when:
        esIndexMappingBuilder.setMapping("serverInfo", "hostName", MappingType.KEYWORD);
        esIndexMappingBuilder.setMapping("serverInfo", "serverIp", MappingType.KEYWORD);
        Map<String, Map<String, Object>> indexMappingJson = esIndexMappingBuilder.build();

        then:
        indexMappingJson != null;
    }

    def "get mapping struct from model" () {
        setup:
        String jsonString = "{\"parentModuleName\":\"apisample\",\"serverInfo\":{\"domain\":null,\"serverPort\":0,\"hostName\":\"leekyoungil-t480s\",\"serverIp\":\"192.168.0.28\"},\"jvmInfo\":{\"javaVmSpecificationName\":\"Java Virtual Machine Specification\",\"javaVmSpecificationVersion\":\"1.8\",\"fileEncoding\":\"UTF-8\",\"javaVmInfo\":\"mixed mode\",\"javaVmVersion\":\"25.171-b11\",\"javaVmName\":\"Java HotSpot(TM) 64-Bit Server VM\",\"PID\":\"16712\",\"jvmFreeMemory\":349,\"userLanguage\":\"en\",\"javaHome\":\"C:\\\\Program Files\\\\Java\\\\jdk1.8.0_171\\\\jre\",\"javaVmVendor\":\"Oracle Corporation\",\"jvmUsedMemory\":22,\"catalinaHome\":\"C:\\\\Users\\\\leeky\\\\AppData\\\\Local\\\\Temp\\\\tomcat.4390500039053921657.8081\",\"userCountry\":\"US\",\"jvmTotalMemory\":372,\"javaVmSpecificationVendor\":\"Oracle Corporation\",\"jvmMaxMemory\":3605,\"userTimezone\":\"Asia/Seoul\"},\"id\":\"69b7bc7e3f524beb822358e3513ef2cd1529127204109\",\"illuminatiUniqueUserId\":null,\"general\":{\"clientIp\":\"0:0:0:0:0:0:0:1\",\"methodName\":\"public java.lang.String me.phoboslabs.illuminati.ApiServerSample.controller.ApiSampleController.test1(java.lang.String,java.lang.Integer) throws java.lang.Exception\",\"methodParams\":\"a : , b : \",\"path\":\"/api/v1/test1\",\"anotherPath\":null,\"queryString\":null},\"header\":{\"illuminatiProcId\":\"f0d72316144f4ccdbc7421a2422efe5e1529127204108-illuminatiProcId\",\"illuminatiSProcId\":null,\"illuminatiGProcId\":\"f5ba6099945a49f6819aea407e4729af1529127204107-illuminatiGProcId\",\"accept\":\"text/html,application/xhtml+xml,application/xml;q\\u003d0.9,image/webp,image/apng,*/*;q\\u003d0.8\",\"acceptCharset\":null,\"acceptEncoding\":\"gzip, deflate, br\",\"acceptLanguage\":\"ko-KR,ko;q\\u003d0.9,en-US;q\\u003d0.8,en;q\\u003d0.7,la;q\\u003d0.6\",\"authorization\":null,\"cookie\":null,\"expect\":null,\"from\":null,\"host\":\"localhost:8081\",\"ifMatch\":null,\"ifModifiedSince\":null,\"ifNoneMatch\":null,\"ifRange\":null,\"ifUnmodifiedSince\":null,\"maxForwards\":null,\"proxyAuthorization\":null,\"range\":null,\"referer\":null,\"te\":null,\"userAgent\":\"Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/67.0.3396.87 Safari/537.36\",\"connection\":\"keep-alive\",\"cacheControl\":\"max-age\\u003d0\",\"upgradeInsecureRequests\":\"1\",\"contentType\":null,\"contentLength\":null,\"postContentBody\":null,\"origin\":null,\"xRequestedWith\":null,\"xRealIp\":null,\"xScheme\":null,\"xForwardedProto\":null,\"xForwardedHost\":null,\"xForwardedServer\":null,\"xForwardedSsl\":null,\"dnt\":null,\"pragma\":null,\"sessionInfo\":null,\"anotherHeader\":null,\"parsedCookie\":null},\"changedJsElement\":null,\"elapsedTime\":3,\"timestamp\":1529127204109,\"logTime\":\"2018-06-16T14:33:24\",\"output\":{\"result\":\"{\\\"status\\\":0,\\\"message\\\":\\\"SUCCESS\\\",\\\"result\\\":{\\\"id\\\":401544,\\\"channel_id\\\":1,\\\"channel_item_id\\\":401544,\\\"seller_id\\\":1846,\\\"seller_name\\\":\\\"안나테스트교환권\\\",\\\"type\\\":\\\"Voucher\\\",\\\"sale_status\\\":\\\"SaleNow\\\",\\\"standard_price\\\":26000,\\\"seller_price\\\":26000,\\\"channel_fee_rate\\\":4.0,\\\"channel_discount_rate\\\":0.00000,\\\"channel_discount_amount\\\":0,\\\"price\\\":26000,\\\"brand_id\\\":68993,\\\"exchange_brand_id\\\":10525,\\\"released_at\\\":\\\"2017-03-24T02:29:00Z\\\",\\\"expired_at\\\":\\\"9999-12-31T14:59:59Z\\\",\\\"has_option\\\":false,\\\"is_adult_product\\\":false,\\\"island_shipping\\\":false,\\\"representative_description\\\":\\\"남녀노소 관계없이 누구나 좋아하는 맛이 한 케이크에!\\\",\\\"representative_image_kage_key\\\":\\\"lJNSk/SubSH1ZW5h/7uqm49BtEZ0pLubkPUJf81\\\",\\\"representative_image_file_name\\\":\\\"lkage783dn1.jpeg\\\",\\\"representative_image_url\\\":\\\"http://alpha-api1-kage.kakaka.com/dn//lJNSk/SubSH1ZW5h/7uqm49BtEZ0pLubkPUJf81/img.jpg\\\",\\\"description_image_url\\\":\\\"\\\",\\\"image_url\\\":\\\"http://alpha-api1-kage.kakaka.com/dn//lJNSk/SubSH1ZW5h/7uqm49BtEZ0pLubkPUJf81/img.jpg\\\",\\\"image_thumb_url\\\":\\\"http://alpha-api1-kage.kakaka.com/dn//lJNSk/SubSH1ZW5h/7uqm49BtEZ0pLubkPUJf81/145x145.jpg\\\",\\\"image_kagekey\\\":\\\"lJNSk/SubSH1ZW5h/7uqm49BtEZ0pLubkPUJf81\\\",\\\"image_file_name\\\":\\\"lkage783dn1.jpeg\\\",\\\"media_file_url\\\":\\\"\\\",\\\"sold_count\\\":0,\\\"saled_count\\\":0,\\\"total_sold_count\\\":0,\\\"standard_category_id\\\":8181,\\\"standard_category_code\\\":\\\"23010100\\\",\\\"is_risk\\\":false,\\\"is_pg_risk\\\":false,\\\"created_at\\\":\\\"2017-03-24T02:29:06Z\\\",\\\"created_by\\\":\\\"kelly.eo\\\",\\\"modified_at\\\":\\\"2017-03-30T11:08:47Z\\\",\\\"modified_by\\\":\\\"kellin.me\\\",\\\"voucher_type\\\":\\\"Exchange\\\",\\\"is_direct_buying\\\":false,\\\"is_limit_sale_count\\\":false,\\\"limit_sale_count_per_user\\\":2,\\\"limit_sale_count_per_order\\\":0,\\\"is_not_in_list\\\":false,\\\"admin_discount_amount_type\\\":0,\\\"admin_discount_amount\\\":0,\\\"is_limited\\\":false,\\\"expiry_days\\\":93,\\\"enable_drop\\\":true,\\\"additional1_image_url\\\":\\\"\\\",\\\"additional2_image_url\\\":\\\"\\\",\\\"additional3_image_url\\\":\\\"\\\",\\\"additional4_image_url\\\":\\\"\\\",\\\"additional5_image_url\\\":\\\"\\\",\\\"certification_infos\\\":[],\\\"is_soldout\\\":false,\\\"is_display\\\":true,\\\"sale_status_label\\\":\\\"판매중\\\",\\\"sale_status_id\\\":\\\"201\\\",\\\"fee_rate\\\":4.0,\\\"channel_name\\\":\\\"선물하기\\\",\\\"brand_name\\\":\\\"주문개선\\\",\\\"category_name\\\":\\\"커피\\\",\\\"exchange_brand_name\\\":\\\"선물하기센터\\\",\\\"total_discount_rate\\\":0,\\\"voucher_type_label\\\":\\\"교환권\\\"}}\"},\"isActiveChaosBomber\":false}"
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

    public TestEsTemplateInterfaceModelImpl () {
        super();
    }
}
