

public class EsTest {

//    @Test
//    public void esSaveTest () {
//        RequestHeaderModel header = new RequestHeaderModel(null);
//        header.setAccept("text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8");
//        header.setAcceptEncoding("gzip, deflate, br");
//        header.setAcceptLanguage("ko-KR,ko;q=0.8,en-US;q=0.6,en;q=0.4,la;q=0.2");
//        header.setHost("localhost:8081");
//        header.setUserAgent("Mozilla/5.0 (Macintosh; Intel Mac OS X 10_12_6) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/59.0.3071.115 Safari/537.36");
//        header.setConnection("keep-alive");
//        header.setCacheControl("max-age=0");
//        header.setUpgradeInsecureRequests("1");
//
//        Map<String, Object> requestMap = new HashMap<String, Object>();
//        requestMap.put("parentModuleName", "apiSample");
//        requestMap.put("domain", "localhost");
//        requestMap.put("hostName", "Kellins-MacBook-Pro-15.local");
//        requestMap.put("serverIp", "127.0.0.1");
//        requestMap.put("clientIp", "0:0:0:0:0:0:0:1");
//        requestMap.put("serverPort", 8081);
//        requestMap.put("path", "/api/v1/test1");
//        requestMap.put("queryString", "?test=1");
//
//        RequestGeneralModel general = new RequestGeneralModel(requestMap);
//
//        long elapsedTime = 3;
//        Object output = "{\"id\":\"9ee627d316c24459aa3b2b3ff12e14f7\",\"general\":{\"domain\":\"localhost\",\"parentModuleName\":\"apisample\",\"hostName\":\"Kellins-MacBook-Pro-15.local\",\"serverIp\":\"127.0.0.1\",\"clientIp\":\"0:0:0:0:0:0:0:1\",\"serverPort\":8081,\"path\":\"http://localhost:8081/api/v1/test1\"},\"header\":{\"accept\":\"text/html,application/xhtml+xml,application/xml;q\\u003d0.9,image/webp,image/apng,*/*;q\\u003d0.8\",\"acceptEncoding\":\"gzip, deflate, br\",\"acceptLanguage\":\"ko-KR,ko;q\\u003d0.8,en-US;q\\u003d0.6,en;q\\u003d0.4,la;q\\u003d0.2\",\"host\":\"localhost:8081\",\"userAgent\":\"Mozilla/5.0 (Macintosh; Intel Mac OS X 10_12_6) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/59.0.3071.115 Safari/537.36\",\"connection\":\"keep-alive\",\"cacheControl\":\"max-age\\u003d0\",\"upgradeInsecureRequests\":\"1\"},\"elapsedTime\":2,\"output\":\"{\\\"status\\\":0,\\\"message\\\":\\\"SUCCESS\\\",\\\"result\\\":{\\\"id\\\":401544,\\\"channel_id\\\":1,\\\"channel_item_id\\\":401544,\\\"seller_id\\\":1846,\\\"seller_name\\\":\\\"안나테스트교환권\\\",\\\"type\\\":\\\"Voucher\\\",\\\"sale_status\\\":\\\"SaleNow\\\",\\\"name\\\":\\\"마이넘버원2\\\",\\\"standard_price\\\":26000,\\\"seller_price\\\":26000,\\\"channel_fee_rate\\\":4.0,\\\"channel_discount_rate\\\":0.00000,\\\"channel_discount_amount\\\":0,\\\"price\\\":26000,\\\"brand_id\\\":68993,\\\"exchange_brand_id\\\":10525,\\\"released_at\\\":\\\"2017-03-24T02:29:00Z\\\",\\\"expired_at\\\":\\\"9999-12-31T14:59:59Z\\\",\\\"has_option\\\":false,\\\"is_adult_product\\\":false,\\\"island_shipping\\\":false,\\\"representative_description\\\":\\\"남녀노소 관계없이 누구나 좋아하는 맛이 한 케이크에!\\\",\\\"representative_image_kage_key\\\":\\\"lJNSk/SubSH1ZW5h/7uqm49BtEZ0pLubkPUJf81\\\",\\\"representative_image_file_name\\\":\\\"lkage783dn1.jpeg\\\",\\\"representative_image_url\\\":\\\"http://alpha-api1-kage.kakaka.com/dn//lJNSk/SubSH1ZW5h/7uqm49BtEZ0pLubkPUJf81/img.jpg\\\",\\\"description_image_url\\\":\\\"\\\",\\\"image_url\\\":\\\"http://alpha-api1-kage.kakaka.com/dn//lJNSk/SubSH1ZW5h/7uqm49BtEZ0pLubkPUJf81/img.jpg\\\",\\\"image_thumb_url\\\":\\\"http://alpha-api1-kage.kakaka.com/dn//lJNSk/SubSH1ZW5h/7uqm49BtEZ0pLubkPUJf81/145x145.jpg\\\",\\\"image_kagekey\\\":\\\"lJNSk/SubSH1ZW5h/7uqm49BtEZ0pLubkPUJf81\\\",\\\"image_file_name\\\":\\\"lkage783dn1.jpeg\\\",\\\"media_file_url\\\":\\\"\\\",\\\"sold_count\\\":0,\\\"saled_count\\\":0,\\\"total_sold_count\\\":0,\\\"standard_category_id\\\":8181,\\\"standard_category_code\\\":\\\"23010100\\\",\\\"is_risk\\\":false,\\\"is_pg_risk\\\":false,\\\"created_at\\\":\\\"2017-03-24T02:29:06Z\\\",\\\"created_by\\\":\\\"kelly.eo\\\",\\\"modified_at\\\":\\\"2017-03-30T11:08:47Z\\\",\\\"modified_by\\\":\\\"kellin.me\\\",\\\"voucher_type\\\":\\\"Exchange\\\",\\\"is_direct_buying\\\":false,\\\"is_limit_sale_count\\\":false,\\\"limit_sale_count_per_user\\\":2,\\\"limit_sale_count_per_order\\\":0,\\\"is_not_in_list\\\":false,\\\"admin_discount_amount_type\\\":0,\\\"admin_discount_amount\\\":0,\\\"is_limited\\\":false,\\\"expiry_days\\\":93,\\\"enable_drop\\\":true,\\\"additional1_image_url\\\":\\\"\\\",\\\"additional2_image_url\\\":\\\"\\\",\\\"additional3_image_url\\\":\\\"\\\",\\\"additional4_image_url\\\":\\\"\\\",\\\"additional5_image_url\\\":\\\"\\\",\\\"certification_infos\\\":[],\\\"is_soldout\\\":false,\\\"is_display\\\":true,\\\"sale_status_label\\\":\\\"판매중\\\",\\\"sale_status_id\\\":\\\"201\\\",\\\"fee_rate\\\":4.0,\\\"channel_name\\\":\\\"선물하기\\\",\\\"brand_name\\\":\\\"주문개선\\\",\\\"category_name\\\":\\\"커피\\\",\\\"exchange_brand_name\\\":\\\"선물하기센터\\\",\\\"total_discount_rate\\\":0,\\\"voucher_type_label\\\":\\\"교환권\\\"}}\"}";
//        String id = "9ee627d316c24459aa3b2b3ff12e14f7";
//
////        IlluminatiEsTemplateInterfaceModelImplImpl illuminatiEsModelImpl = new IlluminatiEsTemplateInterfaceModelImplImpl(general, header, elapsedTime, output, id, 1502120199214L);
////
////        String esUrl = "192.168.99.100";
////        int esPort = 32783;
////
////        CloseableHttpClient httpClient = new IlluminatiHttpClient();
////
////        EsClient eSclient = new ESclientImpl(httpClient, esUrl, esPort);
////        eSclient.save(illuminatiEsModelImpl);
////
////        System.out.println("test");
//    }
}
