package com.leekyoungil.illuminati.ApiServerSample.controller;

import com.leekyoungil.illuminati.client.annotation.Illuminati;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping(value = "/api/v1/form/", produces = MediaType.TEXT_HTML_VALUE)
public class ApiFormSampleController {

    @Illuminati
    @RequestMapping(value = "test1", method = RequestMethod.POST, consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    @ResponseBody
    public void test1 () {
        String testJson = "{\"status\":0,\"message\":\"SUCCESS\",\"result\":{\"id\":401544,\"channel_id\":1,\"channel_item_id\":401544,\"seller_id\":1846,\"seller_name\":\"안나테스트교환권\",\"type\":\"Voucher\",\"sale_status\":\"SaleNow\",\"standard_price\":26000,\"seller_price\":26000,\"channel_fee_rate\":4.0,\"channel_discount_rate\":0.00000,\"channel_discount_amount\":0,\"price\":26000,\"brand_id\":68993,\"exchange_brand_id\":10525,\"released_at\":\"2017-03-24T02:29:00Z\",\"expired_at\":\"9999-12-31T14:59:59Z\",\"has_option\":false,\"is_adult_product\":false,\"island_shipping\":false,\"representative_description\":\"남녀노소 관계없이 누구나 좋아하는 맛이 한 케이크에!\",\"representative_image_kage_key\":\"lJNSk/SubSH1ZW5h/7uqm49BtEZ0pLubkPUJf81\",\"representative_image_file_name\":\"lkage783dn1.jpeg\",\"representative_image_url\":\"http://alpha-api1-kage.kakaka.com/dn//lJNSk/SubSH1ZW5h/7uqm49BtEZ0pLubkPUJf81/img.jpg\",\"description_image_url\":\"\",\"image_url\":\"http://alpha-api1-kage.kakaka.com/dn//lJNSk/SubSH1ZW5h/7uqm49BtEZ0pLubkPUJf81/img.jpg\",\"image_thumb_url\":\"http://alpha-api1-kage.kakaka.com/dn//lJNSk/SubSH1ZW5h/7uqm49BtEZ0pLubkPUJf81/145x145.jpg\",\"image_kagekey\":\"lJNSk/SubSH1ZW5h/7uqm49BtEZ0pLubkPUJf81\",\"image_file_name\":\"lkage783dn1.jpeg\",\"media_file_url\":\"\",\"sold_count\":0,\"saled_count\":0,\"total_sold_count\":0,\"standard_category_id\":8181,\"standard_category_code\":\"23010100\",\"is_risk\":false,\"is_pg_risk\":false,\"created_at\":\"2017-03-24T02:29:06Z\",\"created_by\":\"kelly.eo\",\"modified_at\":\"2017-03-30T11:08:47Z\",\"modified_by\":\"kellin.me\",\"voucher_type\":\"Exchange\",\"is_direct_buying\":false,\"is_limit_sale_count\":false,\"limit_sale_count_per_user\":2,\"limit_sale_count_per_order\":0,\"is_not_in_list\":false,\"admin_discount_amount_type\":0,\"admin_discount_amount\":0,\"is_limited\":false,\"expiry_days\":93,\"enable_drop\":true,\"additional1_image_url\":\"\",\"additional2_image_url\":\"\",\"additional3_image_url\":\"\",\"additional4_image_url\":\"\",\"additional5_image_url\":\"\",\"certification_infos\":[],\"is_soldout\":false,\"is_display\":true,\"sale_status_label\":\"판매중\",\"sale_status_id\":\"201\",\"fee_rate\":4.0,\"channel_name\":\"선물하기\",\"brand_name\":\"주문개선\",\"category_name\":\"커피\",\"exchange_brand_name\":\"선물하기센터\",\"total_discount_rate\":0,\"voucher_type_label\":\"교환권\"}}";

        System.out.println(testJson);
    }
}
