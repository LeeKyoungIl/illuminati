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

package me.phoboslabs.illuminati.elasticsearch.infra.param.groupby;

import me.phoboslabs.illuminati.common.util.ConvertUtil;
import me.phoboslabs.illuminati.common.util.StringObjectUtils;
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
        if (!StringObjectUtils.isValid(groupByKey)) {
            return this;
        }
        this.esGroupBy.setGroupBy(groupByKey);
        return this;
    }

    public Map<String, Object> build () throws Exception {
        if (CollectionUtils.isEmpty(this.esGroupBy.getGroupByList())) {
            throw new Exception("check esGroupBy.");
        }
        Map<String, Object> lastResultMap = null;
        for (Map<String, Object> groupBy : this.esGroupBy.getGroupByList()) {
            if (lastResultMap != null) {
                for (String key : lastResultMap.keySet()) {
                    Map<String, Object> keyMap = ConvertUtil.castToMapOf(String.class, Object.class, Map.class.cast(lastResultMap.get(key)));
                    keyMap.put(AGGREGATION_KEY_NAME, groupBy);
                    Map<String, Object> aggsMap = new HashMap<>();
                    aggsMap.put(key, keyMap);
                    groupBy = aggsMap;
                }
            }
            lastResultMap = groupBy;
        }

        return lastResultMap;
    }
}
