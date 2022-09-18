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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EsGroupBy {

    private final List<Map<String, Object>> aggList = new ArrayList<>();

    private final static String FIELD_KEY_NAME = "field";
    private final static String TERMS_KEY_NAME = "terms";

    public EsGroupBy() {

    }

    public void setGroupBy(String groupByKey) {
        Map<String, Object> field = new HashMap<>();
        field.put(FIELD_KEY_NAME, groupByKey);
        Map<String, Object> terms = new HashMap<>();
        terms.put(TERMS_KEY_NAME, field);
        Map<String, Object> agg = new HashMap<>();
        agg.put(groupByKey, terms);
        this.aggList.add(agg);
    }

    public List<Map<String, Object>> getGroupByList() {
        return this.aggList;
    }
}
