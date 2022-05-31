package me.phoboslabs.illuminati.ApiServerSample;

import org.junit.Test;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

public class ApiServerSampleApplicationTests {

//    @Test
//    public void test () {
//        ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
//        IlluminatiProperties illuminatiProperties = null;
//
//        try {
//            InputStream input = this.getClass().getClassLoader().getResourceAsStream("illuminati-test.yml");
//            illuminatiProperties = mapper.readValue(input, IlluminatiProperties.class);
//            System.out.println("test");
//        } catch (Exception e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        }
//
//        System.out.println("test");
//    }

//    @Test
//    public void fileUtilTest() {
//        IlluminatiProperties illuminatiProperties = FileUtils.getIlluminatiPropertiesFromModel("illuminati.bak");
//
//        System.out.println(illuminatiProperties);
//    }
//
//    public IlluminatiProperties getIlluminatiPropertiesFromModel1 (String configPropertiesFileName) {
//        Properties prop = new Properties();
//        InputStream input = null;
//
//        try {
//            File file = new File(configPropertiesFileName);
//            if (file.exists()) {
//                System.out.println("tet");
//            } else {
//                System.out.println(file.getAbsolutePath());
//            }
//
//            input = new FileInputStream(file);
//
//            if(input==null){
//                System.out.println("Sorry, unable to find " + configPropertiesFileName);
//                return null;
//            }
//
//            prop.load(input);
//        } catch (IOException ex) {
//            ex.printStackTrace();
//        } finally {
//            if (input != null) {
//                try {
//                    input.close();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//        }
//
//        if (prop.isEmpty()) {
//            return null;
//        }
//
//        return new IlluminatiProperties(prop);
//    }

//    @Test
//    public void testDataTest () {
//        String testJson = "{\"status\":0,\"message\":\"SUCCESS\",\"result\":{\"id\":401544,\"channel_id\":1,\"channel_item_id\":401544,\"seller_id\":1846,\"seller_name\":\"안나테스트교환권\",\"type\":\"Voucher\",\"sale_status\":\"SaleNow\",\"name\":\"마이넘버원2\",\"standard_price\":26000,\"seller_price\":26000,\"channel_fee_rate\":4.0,\"channel_discount_rate\":0.00000,\"channel_discount_amount\":0,\"price\":26000,\"brand_id\":68993,\"exchange_brand_id\":10525,\"released_at\":\"2017-03-24T02:29:00Z\",\"expired_at\":\"9999-12-31T14:59:59Z\",\"has_option\":false,\"is_adult_product\":false,\"island_shipping\":false,\"representative_description\":\"남녀노소 관계없이 누구나 좋아하는 맛이 한 케이크에!\",\"representative_image_kage_key\":\"lJNSk/SubSH1ZW5h/7uqm49BtEZ0pLubkPUJf81\",\"representative_image_file_name\":\"lkage783dn1.jpeg\",\"representative_image_url\":\"http://alpha-api1-kage.kakaka.com/dn//lJNSk/SubSH1ZW5h/7uqm49BtEZ0pLubkPUJf81/img.jpg\",\"description_image_url\":\"\",\"image_url\":\"http://alpha-api1-kage.kakaka.com/dn//lJNSk/SubSH1ZW5h/7uqm49BtEZ0pLubkPUJf81/img.jpg\",\"image_thumb_url\":\"http://alpha-api1-kage.kakaka.com/dn//lJNSk/SubSH1ZW5h/7uqm49BtEZ0pLubkPUJf81/145x145.jpg\",\"image_kagekey\":\"lJNSk/SubSH1ZW5h/7uqm49BtEZ0pLubkPUJf81\",\"image_file_name\":\"lkage783dn1.jpeg\",\"media_file_url\":\"\",\"sold_count\":0,\"saled_count\":0,\"total_sold_count\":0,\"standard_category_id\":8181,\"standard_category_code\":\"23010100\",\"is_risk\":false,\"is_pg_risk\":false,\"created_at\":\"2017-03-24T02:29:06Z\",\"created_by\":\"kelly.eo\",\"modified_at\":\"2017-03-30T11:08:47Z\",\"modified_by\":\"kellin.me\",\"voucher_type\":\"Exchange\",\"is_direct_buying\":false,\"is_limit_sale_count\":false,\"limit_sale_count_per_user\":2,\"limit_sale_count_per_order\":0,\"is_not_in_list\":false,\"admin_discount_amount_type\":0,\"admin_discount_amount\":0,\"is_limited\":false,\"expiry_days\":93,\"enable_drop\":true,\"additional1_image_url\":\"\",\"additional2_image_url\":\"\",\"additional3_image_url\":\"\",\"additional4_image_url\":\"\",\"additional5_image_url\":\"\",\"certification_infos\":[],\"is_soldout\":false,\"is_display\":true,\"sale_status_label\":\"판매중\",\"sale_status_id\":\"201\",\"fee_rate\":4.0,\"channel_name\":\"선물하기\",\"brand_name\":\"주문개선\",\"category_name\":\"커피\",\"exchange_brand_name\":\"선물하기센터\",\"total_discount_rate\":0,\"voucher_type_label\":\"교환권\"}}";
//
//        long start = System.nanoTime();
//
//        for (int i=0; i<1; i++) {
//            byte[] tmp = testJson.getBytes();
//        }
//
//        long end = System.nanoTime();
//
//        System.out.println(end - start);
//
//        long start1 = System.nanoTime();
//
//        for (int i=0; i<1; i++) {
//            byte[] tmp = StringObjectUtils.encode(testJson.toCharArray());
//        }
//
//        long end1 = System.nanoTime();
//
//        System.out.println(end1 - start1);
//    }

//    @Test
//    public void test() {
//        BlockingQueue<String> writeQueue = new ArrayBlockingQueue<String>(2, true);
//
//        try {
//            System.out.println(writeQueue.offer("1", 1000, TimeUnit.MILLISECONDS));
//            System.out.println(writeQueue.offer("12", 1000, TimeUnit.MILLISECONDS));
//            System.out.println(writeQueue.offer("13", 1000, TimeUnit.MILLISECONDS));
//            System.out.println(writeQueue.offer("14", 1000, TimeUnit.MILLISECONDS));
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//        writeQueue.add("1");
//        writeQueue.add("2");
//        writeQueue.add("3");
//        writeQueue.add("4");
//    }
}
