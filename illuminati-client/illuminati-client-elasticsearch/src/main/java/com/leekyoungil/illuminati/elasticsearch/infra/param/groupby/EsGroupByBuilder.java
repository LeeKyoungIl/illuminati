package com.leekyoungil.illuminati.elasticsearch.infra.param.groupby;

import com.leekyoungil.illuminati.common.util.ConvertUtil;
import com.leekyoungil.illuminati.common.util.StringObjectUtils;
import org.apache.commons.collections.CollectionUtils;

import java.util.HashMap;
import java.util.Map;

public class EsGroupByBuilder {

    private final EsGroupBy esGroupBy = new EsGroupBy();

    private final static String AGGREGATION_KEY_NAME = "aggs";

    private EsGroupByBuilder () {

    }

    public static EsGroupByBuilder Builder() {
        return new EsGroupByBuilder();
    }

    public EsGroupByBuilder setGroupByKey (String groupByKey) {
        if (StringObjectUtils.isValid(groupByKey) == false) {
            return this;
        }
        this.esGroupBy.setGroupBy(groupByKey);
        return this;
    }

    public Map<String, Object> build () {
        if (CollectionUtils.isNotEmpty(this.esGroupBy.getGroupByList())) {
            Map<String, Object> lastResultMap = null;
            for (Map<String, Object> groupBy : this.esGroupBy.getGroupByList()) {
                if (lastResultMap != null) {
                    for (String key : lastResultMap.keySet()) {
                        Map<String, Object> keyMap = ConvertUtil.castToMapOf(String.class, Object.class, Map.class.cast(lastResultMap.get(key)));
                        keyMap.put(AGGREGATION_KEY_NAME, groupBy);
                        Map<String, Object> aggsMap = new HashMap<String, Object>();
                        aggsMap.put(key, keyMap);
                        groupBy = aggsMap;
                    }
                }
                lastResultMap = groupBy;
            }

            return lastResultMap;
        }

        return null;
    }
}
