package com.leekyoungil.illuminati.elasticsearch.infra.model;

import com.google.gson.reflect.TypeToken;
import com.leekyoungil.illuminati.common.constant.IlluminatiConstant;
import com.leekyoungil.illuminati.common.util.StringObjectUtils;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.bind.ValidationException;
import java.lang.reflect.Type;
import java.util.*;

public class EsDataImpl implements EsData {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final String sourceData;
    private List<Map<String, Object>> sourceList;

    public EsDataImpl (final String sourceData) throws ValidationException {
        if (StringObjectUtils.isValid(sourceData) == false) {
            throw new ValidationException("source data is a required value.");
        }

        this.sourceData = sourceData;
        this.initEsData();
    }

    private void initEsData() {
        Type type = new TypeToken<Map<String, Object>>(){}.getType();
        Map<String, Object> resultMap = IlluminatiConstant.ILLUMINATI_GSON_OBJ.fromJson(this.sourceData, type);
        if (resultMap.containsKey("hits") == false) {
            return;
        }
        Map<String, Object> bufEsDataMap = (Map<String, Object>) resultMap.get("hits");
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
            if (map.containsKey("_source") == true && ((Map<String, Object>) map.get("_source")).size() > 0) {
                map.remove("_score");
                this.sourceList.add(map);
            }
        }
    }

    @Override public List<Map<String, Object>> getEsDataList() {
        return this.sourceList;
    }
}
