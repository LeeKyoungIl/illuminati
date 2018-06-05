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

    private final static String LOG_TIME_KEY_NAME = "logTime";
    private final static String FILTER_KEY_NAME = "filter";
    private final static String FILTERED_KEY_NAME = "filtered";
    private final static String QUERY_KEY_NAME = "query";

    private EsQueryBuilder () {

    }
    private EsQueryBuilder (EsQueryType esQueryType) {
        this.setQueryType(esQueryType);
        if (esQueryType == EsQueryType.MATCH_ALL) {
            this.setMatchAll();
        }
    }

    public static EsQueryBuilder Builder(){
        return new EsQueryBuilder();
    }

    public static EsQueryBuilder Builder(EsQueryType esQueryType){
        return new EsQueryBuilder(esQueryType);
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
        } else if (this.match.containsKey(EsQueryType.getMatchAllText()) == true) {
            this.match.remove(EsQueryType.getMatchAllText());
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
        this.match.put(EsQueryType.getMatchAllText(), new HashMap<String, Object>());
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
        range.put(LOG_TIME_KEY_NAME, this.range);
        this.filter = new HashMap<String, Object>();
        this.filter.put(FILTER_KEY_NAME, range);
    }

    public Map<String, Object> build () {
        if (this.queryType == EsQueryType.MATCH_ALL) {
            Map<String, Object> innerQuery = new HashMap<String, Object>();
            innerQuery.put(QUERY_KEY_NAME, this.match);
            if (this.filter != null && this.filter.size() > 0) {
                innerQuery.put(FILTER_KEY_NAME, this.filter);
            }
            Map<String, Object> outerQuery = new HashMap<String, Object>();
            outerQuery.put(FILTERED_KEY_NAME, innerQuery);

            return outerQuery;
        }

        return null;
    }
}
