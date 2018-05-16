package com.leekyoungil.illuminati.gatekeeper.api.service;

import com.leekyoungil.illuminati.elasticsearch.infra.EsClient;
import com.leekyoungil.illuminati.elasticsearch.infra.model.EsData;
import com.leekyoungil.illuminati.elasticsearch.infra.model.EsDataImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;
import java.util.*;

public class JvmInfoApiService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final EsClient eSclient;

    private final static Map<String, Object> JVM_ES_FIELD_PARAM = new HashMap<>();
    static {
        List<String> jvmFieldList = new ArrayList<String>();
        jvmFieldList.add("jvmInfo");
        jvmFieldList.add("timestamp");
        JVM_ES_FIELD_PARAM.put("source", jvmFieldList);
    }
    private final static SimpleDateFormat SIMPLE_DATA_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public JvmInfoApiService (EsClient eSclient) {
        this.eSclient = eSclient;
    }

    public List<Map<String, Object>> getJvmInfoFromElasticsearch() {
        List<Map<String, Object>> resultList = null;

        try {
            String returnData = eSclient.getDataByParam(JVM_ES_FIELD_PARAM);
            EsData esData = new EsDataImpl(returnData);
            resultList = esData.getEsDataList();

            for (Map<String, Object> data : resultList) {
                this.convertJsonTimestampToDate((Map<String, Object>) data.get("source"));
            }
        } catch (Exception ex) {
            this.logger.error("Exception occurred at getting data from Elasticsearch.", ex.getMessage());
        }

        return resultList;
    }

    private void convertJsonTimestampToDate (Map<String, Object> mapData) {
        final String checkKey = "timestamp";

        if (mapData.containsKey(checkKey) == true) {
            try {
                double dbTimestamp = (double) mapData.get(checkKey);
                Date targetDate = new Date((long) dbTimestamp);
                String lastStringDate = SIMPLE_DATA_FORMAT.format(targetDate);
                mapData.put(checkKey, lastStringDate);
            } catch (Exception ex) {
                this.logger.error("Exception occurred at casting timestamp data.");
            }
        }

    }
}
