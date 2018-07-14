package me.phoboslabs.illuminati.elasticsearch.infra.param.groupby;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EsGroupBy {

    private final List<Map<String, Object>> aggList = new ArrayList<Map<String, Object>>();

    private final static String FIELD_KEY_NAME = "field";
    private final static String TERMS_KEY_NAME = "terms";

    public EsGroupBy () {

    }

    public void setGroupBy (String groupByKey) {
        Map<String, Object> field = new HashMap<String, Object>();
        field.put(FIELD_KEY_NAME, groupByKey);
        Map<String, Object> terms = new HashMap<String, Object>();
        terms.put(TERMS_KEY_NAME, field);
        Map<String, Object> agg = new HashMap<String, Object>();
        agg.put(groupByKey, terms);
        this.aggList.add(agg);
    }

    public List<Map<String, Object>> getGroupByList () {
        return this.aggList;
    }
}
