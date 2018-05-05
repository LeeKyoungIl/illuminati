package com.leekyoungil.illuminati.gatekeeper.api.service;

import com.leekyoungil.illuminati.elasticsearch.infra.EsClient;
import com.leekyoungil.illuminati.elasticsearch.infra.model.EsData;
import com.leekyoungil.illuminati.elasticsearch.infra.model.EsDataImpl;
import groovy.util.logging.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.bind.ValidationException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class JvmInfoApiService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final EsClient eSclient;

    private final static List<String> FINAL_STATIC_FIELD_NAME = Arrays.asList("jvmInfo", "timestamp");

    public JvmInfoApiService (EsClient eSclient) {
        this.eSclient = eSclient;
    }

    public List<Map<String, Object>> getJvmInfoFromElasticsearch() {
        List<Map<String, Object>> resultList = null;

        try {
            String returnData = eSclient.getAllDataByFields(FINAL_STATIC_FIELD_NAME);
            EsData esData = new EsDataImpl(returnData);
            resultList = esData.getEsDataList();
        } catch (Exception ex) {
            logger.error("Exception occurred at getting data from Elasticsearch.", ex.getMessage());
        }

        return resultList;
    }
}
