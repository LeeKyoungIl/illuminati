package com.leekyoungil.illuminati.elasticsearch.infra.param.query;

import com.leekyoungil.illuminati.elasticsearch.infra.enums.EsQueryType;
import com.leekyoungil.illuminati.elasticsearch.infra.enums.EsRangeType;

import java.util.HashMap;
import java.util.Map;

public class EsQueryBuilder {

    private EsQueryType queryType = EsQueryType.MATCH_ALL;
    private Map<String, Object> match;
    private Map<String, Object> range;
    private Map<String, Object> filter;

    private EsQueryBuilder () {

    }

    public static EsQueryBuilder Builder(){
        return new EsQueryBuilder();
    }

    public EsQueryBuilder setQueryType (EsQueryType esQueryType) {
        this.queryType = esQueryType;
        return this;
    }

    public EsQueryBuilder setMatch (String key, Object value) {
        if (this.queryType != EsQueryType.MATCH) {
            return this;
        }
        if (this.match == null) {
            this.match = new HashMap<String, Object>();
        } else if (this.match.containsKey("match_all") == true) {
            this.match.remove("match_all");
        }
        this.match.put(key, value);
        return this;
    }

    public EsQueryBuilder setMatchAll () {
        if (this.queryType != EsQueryType.MATCH_ALL) {
            return this;
        }
        if (this.match == null) {
            this.match = new HashMap<String, Object>();
        } else {
            this.match.clear();
        }
        this.match.put("match_all", new HashMap<String, Object>());
        return this;
    }

    public EsQueryBuilder setRange (EsRangeType rangeType, Object value) {
        if (this.range == null) {
            this.range = new HashMap<String, Object>();
        }
        this.range.put(rangeType.getRangeType(), value);
        return this;
    }

    private void makeRangeQuery () {
        if (this.range == null && this.range.size() == 0) {
            return;
        }
        Map<String, Object> range = new HashMap<String, Object>();
        range.put("logTime", this.range);
        this.filter = new HashMap<String, Object>();
        this.filter.put("filter", range);
    }

    public Map<String, Object> build () {
        if (this.queryType == EsQueryType.MATCH_ALL) {
            Map<String, Object> innerQuery = new HashMap<String, Object>();
            innerQuery.put("query", this.match);
            if (this.filter != null && this.filter.size() > 0) {
                innerQuery.put("filter", this.filter);
            }
            Map<String, Object> outerQuery = new HashMap<String, Object>();
            outerQuery.put("filtered", innerQuery);

            return outerQuery;
        }

        return null;
    }
}
