package com.leekyoungil.illuminati.elasticsearch.infra.param.query;

import com.leekyoungil.illuminati.elasticsearch.infra.enums.EsQueryType;
import com.leekyoungil.illuminati.elasticsearch.infra.enums.EsRangeType;

import java.util.HashMap;
import java.util.Map;

public class EsQueryBuilder {

    private EsQueryType queryType;
    private Map<String, Object> match;
    private Map<String, Object> range;

    public static EsQueryBuilder Builder(){
        return new EsQueryBuilder();
    }

    public EsQueryBuilder setQueryType (EsQueryType esQueryType) {
        this.queryType = esQueryType;
        if (this.match == null) {
            this.match = new HashMap<String, Object>();
        } else {
            this.match.clear();
        }
        this.match.put("match_all", new HashMap<String, Object>());
        return this;
    }

    public EsQueryBuilder setMatch (String key, Object value) {
        if (this.queryType == EsQueryType.MATCH_ALL) {
            return this;
        }
        if (this.match == null) {
            this.match = new HashMap<String, Object>();
        }
        this.match.put(key, value);
        return this;
    }

    public EsQueryBuilder setRange (EsRangeType rangeType, Object value) {
        if (this.range == null) {
            this.range = new HashMap<String, Object>();
        }
        this.range.put(rangeType.getRangeType(), value);
        return this;
    }

    public Map<String, Object> build () {
        if (this.queryType == EsQueryType.MATCH_ALL) {
            Map<String, Object> innerQuery = new HashMap<String, Object>();
            innerQuery.put("query", this.match);
            Map<String, Object> outerQuery = new HashMap<String, Object>();
            outerQuery.put("filtered", innerQuery);

            return outerQuery;
        }

        return null;
    }
}
