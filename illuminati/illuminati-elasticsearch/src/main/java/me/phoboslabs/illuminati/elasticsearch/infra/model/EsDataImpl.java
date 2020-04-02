/*
 * Copyright 2017 Phoboslabs.me
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package me.phoboslabs.illuminati.elasticsearch.infra.model;

import com.google.gson.JsonSyntaxException;
import me.phoboslabs.illuminati.common.constant.IlluminatiConstant;
import me.phoboslabs.illuminati.common.util.ConvertUtil;
import me.phoboslabs.illuminati.common.util.StringObjectUtils;
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

    private final static String AGGREGATIONS_KEYWORD = "aggregations";
    private final static String HITS_KEYWORD = "hits";
    private final static String MAX_SCORE_KEYWORD = "max_score";
    private final static String SCORE_KEYWORD = "_score";
    private final static String SOURCE_KEYWORD = "_source";
    private final static String BUCKETS_KEYWORD = "buckets";

    private static final Map<String, String> RENAME_KEYS_FROM_ES = new HashMap<String, String>();

    static {
        RENAME_KEYS_FROM_ES.put("_index", "index");
        RENAME_KEYS_FROM_ES.put("_type", "type");
        RENAME_KEYS_FROM_ES.put("_id", "id");
        RENAME_KEYS_FROM_ES.put("_source", "source");
    }

    public EsDataImpl (final String sourceData) throws ValidationException {
        if (!StringObjectUtils.isValid(sourceData)) {
            throw new ValidationException("source data is a required value.");
        }

        this.sourceData = sourceData;
        this.initEsData();
    }

    private void initEsData() {
        try {
            Map<String, Object> resultMap = IlluminatiConstant.ILLUMINATI_GSON_OBJ.fromJson(this.sourceData, IlluminatiConstant.TYPE_FOR_TYPE_TOKEN);
            if (resultMap.containsKey(AGGREGATIONS_KEYWORD)) {
                this.initAggregationData(ConvertUtil.castToMapOf(String.class, Object.class, Map.class.cast(resultMap.get(AGGREGATIONS_KEYWORD))));
            } else {
                this.initBasicSearchData(resultMap);
            }
        } catch (JsonSyntaxException ex) {
            this.logger.error("Failed to Read json - JsonSyntaxException {}", ex.getCause().getMessage(), ex);
        }
    }

    @Override public List<Map<String, Object>> getEsDataList() {
        return this.sourceList;
    }

    private void initAggregationData (Map<String, Object> resultMap) {
        for(String key : resultMap.keySet()) {
            this.sourceList = (List<Map<String, Object>>) (ConvertUtil.castToMapOf(String.class, Object.class, Map.class.cast(resultMap.get(key)))).get(BUCKETS_KEYWORD);
        }
    }

    private void initBasicSearchData (Map<String, Object> resultMap) {
        if (!resultMap.containsKey(HITS_KEYWORD)) {
            return;
        }
        Map<String, Object> bufEsDataMap = ConvertUtil.castToMapOf(String.class, Object.class, Map.class.cast(resultMap.get(HITS_KEYWORD)));
        if (!bufEsDataMap.containsKey(HITS_KEYWORD)) {
            return;
        }
        if (bufEsDataMap.containsKey(MAX_SCORE_KEYWORD)) {
            bufEsDataMap.remove(MAX_SCORE_KEYWORD);
        }
        List<Map<String, Object>> mapList = (List<Map<String, Object>>) bufEsDataMap.get(HITS_KEYWORD);
        if (CollectionUtils.isEmpty(mapList)) {
            return;
        }
        this.sourceList = new ArrayList<Map<String, Object>>();
        for (Map<String, Object> map : mapList) {
            Map<String, Object> source = ConvertUtil.castToMapOf(String.class, Object.class, Map.class.cast(map.get(SOURCE_KEYWORD)));
            if (map.containsKey(SOURCE_KEYWORD) && source.size() > 0) {
                map.remove(SCORE_KEYWORD);
                this.renameKeys(map);
                this.sourceList.add(map);
            }
        }
    }

    private void renameKeys (Map<String, Object> targetMap) {
        for (String key : RENAME_KEYS_FROM_ES.keySet()) {
            if (targetMap.containsKey(key)) {
                targetMap.put(RENAME_KEYS_FROM_ES.get(key), targetMap.get(key));
                targetMap.remove(key);
            }
        }
    }
}
