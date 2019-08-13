package me.phoboslabs.illuminati.gatekeeper.api.service;

import me.phoboslabs.illuminati.common.util.ConvertUtil;
import me.phoboslabs.illuminati.elasticsearch.infra.EsClient;
import me.phoboslabs.illuminati.elasticsearch.infra.model.EsData;
import me.phoboslabs.illuminati.elasticsearch.infra.model.EsDataImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

public abstract class BasicElasticsearchService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final static SimpleDateFormat SIMPLE_DATA_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private final EsClient eSclient;

    BasicElasticsearchService (EsClient eSclient) {
        this.eSclient = eSclient;
    }

    List<Map<String, Object>> requestToElasticsearch(String jsonString) {
        List<Map<String, Object>> resultList = null;

        try {
            String returnData = eSclient.getDataByJson(jsonString);
            EsData esData = new EsDataImpl(returnData);
            resultList = esData.getEsDataList();

            if (resultList == null) {
                return null;
            } else {
                for (Map<String, Object> data : resultList) {
                    if (data.containsKey("source")) {
                        this.convertJsonTimestampToDate(ConvertUtil.castToMapOf(String.class, Object.class, Map.class.cast(data.get("source"))));
                    }
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
