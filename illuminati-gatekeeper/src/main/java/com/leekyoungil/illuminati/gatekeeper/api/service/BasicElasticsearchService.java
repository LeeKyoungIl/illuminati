package com.leekyoungil.illuminati.gatekeeper.api.service;

import com.leekyoungil.illuminati.common.util.ConvertUtil;
import com.leekyoungil.illuminati.elasticsearch.infra.EsClient;
import com.leekyoungil.illuminati.elasticsearch.infra.model.EsData;
import com.leekyoungil.illuminati.elasticsearch.infra.model.EsDataImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

public abstract class BasicElasticsearchService {

    protected final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final static SimpleDateFormat SIMPLE_DATA_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private final EsClient eSclient;

    BasicElasticsearchService (EsClient eSclient) {
        this.eSclient = eSclient;
    }

    protected List<Map<String, Object>> requestToElasticsearch (String jsonString) {
        List<Map<String, Object>> resultList = null;

        try {
            String returnData = eSclient.getDataByJson(jsonString);
            EsData esData = new EsDataImpl(returnData);
            resultList = esData.getEsDataList();

            for (Map<String, Object> data : resultList) {
                Map<String, Object> checkResultMap = data;
                if (data.containsKey("source")) {
                    this.convertJsonTimestampToDate(ConvertUtil.castToMapOf(String.class, Object.class, Map.class.cast(data.get("source"))));
                }
            }
        } catch (Exception ex) {
            this.logger.error("Exception occurred at getting data from Elasticsearch.", ex.getMessage());
            return null;
        }

        return resultList;
    }

    private void convertJsonTimestampToDate (Map<String, Object> mapData) {
        final String checkKey = "timestamp";

        if (mapData.containsKey(checkKey)) {
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
