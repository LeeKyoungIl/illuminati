package com.leekyoungil.illuminati.elasticsearch.infra.model;

import com.google.gson.reflect.TypeToken;
import com.leekyoungil.illuminati.common.constant.IlluminatiConstant;
import com.leekyoungil.illuminati.common.util.ConvertUtil;
import com.leekyoungil.illuminati.common.util.StringObjectUtils;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.bind.ValidationException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EsDataImpl implements EsData {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final String sourceData;
    private List<Map<String, Object>> sourceList;

    private static final Map<String, String> RENAME_KEYS_FROM_ES = new HashMap<String, String>();

    static {
        RENAME_KEYS_FROM_ES.put("_index", "index");
        RENAME_KEYS_FROM_ES.put("_type", "type");
        RENAME_KEYS_FROM_ES.put("_id", "id");
        RENAME_KEYS_FROM_ES.put("_source", "source");
    }

    public EsDataImpl (final String sourceData) throws ValidationException {
        if (StringObjectUtils.isValid(sourceData) == false) {
            throw new ValidationException("source data is a required value.");
        }

        this.sourceData = sourceData;
        this.initEsData();
    }

    private void initEsData() {
        Map<String, Object> resultMap = IlluminatiConstant.ILLUMINATI_GSON_OBJ.fromJson(this.sourceData, new TypeToken<Map<String, Object>>(){}.getType());
        if (resultMap.containsKey("aggregations") == true) {
            this.initAggregationData(ConvertUtil.castToMapOf(String.class, Object.class, Map.class.cast(resultMap.get("aggregations"))));
        } else {
            this.initBasicSearchData(resultMap);
        }
    }

    @Override public List<Map<String, Object>> getEsDataList() {
        return this.sourceList;
    }

    private void initAggregationData (Map<String, Object> resultMap) {
        for(String key : resultMap.keySet()) {
            this.sourceList = (List<Map<String, Object>>) (ConvertUtil.castToMapOf(String.class, Object.class, Map.class.cast(resultMap.get(key)))).get("buckets");
        }
    }

    private void initBasicSearchData (Map<String, Object> resultMap) {
        if (resultMap.containsKey("hits") == false) {
            return;
        }
        Map<String, Object> bufEsDataMap = ConvertUtil.castToMapOf(String.class, Object.class, Map.class.cast(resultMap.get("hits")));
        if (bufEsDataMap.containsKey("hits") == false) {
            return;
        }
        if (bufEsDataMap.containsKey("max_score") == true) {
            bufEsDataMap.remove("max_score");
        }
        List<Map<String, Object>> mapList = (List<Map<String, Object>>) bufEsDataMap.get("hits");
        if (CollectionUtils.isEmpty(mapList) == true) {
            return;
        }
        this.sourceList = new ArrayList<Map<String, Object>>();
        for (Map<String, Object> map : mapList) {
            Map<String, Object> source = ConvertUtil.castToMapOf(String.class, Object.class, Map.class.cast(map.get("_source")));
            if (map.containsKey("_source") == true && source.size() > 0) {
                map.remove("_score");
                this.renameKeys(map);
                this.sourceList.add(map);
            }
        }
    }

    private void renameKeys (Map<String, Object> targetMap) {
        for (String key : RENAME_KEYS_FROM_ES.keySet()) {
            if (targetMap.containsKey(key) == true) {
                targetMap.put(RENAME_KEYS_FROM_ES.get(key), targetMap.get(key));
                targetMap.remove(key);
            }
        }
    }
}
