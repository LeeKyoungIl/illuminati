package me.phoboslabs.illuminati.elasticsearch.infra.param.query;

import me.phoboslabs.illuminati.elasticsearch.infra.enums.EsQueryType;
import me.phoboslabs.illuminati.elasticsearch.infra.enums.EsRangeType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EsQueryBuilder {

    private String esVersion = "5.x";

    private EsQueryType queryType = EsQueryType.MATCH_ALL;
    private Map<String, Object> match;
    private Map<String, Object> range;
    private Map<String, Object> filter;

    private final static String LOG_TIME_KEY_NAME = "logTime";
    private final static String FILTER_KEY_NAME = "filter";

    private final static String FILTERED_KEY_NAME_2_x = "filtered";
    private final static String QUERY_KEY_NAME_2_x = "query";

    private final static String QUERY_KEY_NAME_5_x = "bool";
    private final static String FILTERED_KEY_NAME_5_x = "must";

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

    public EsQueryBuilder setEsVersion (String esVersion) {
        this.esVersion = esVersion;
        return this;
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
        } else if (this.match.containsKey(EsQueryType.getMatchAllText())) {
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

    private String getQueryKeyName () {
        if (this.esVersion.indexOf("2") > -1) {
            return QUERY_KEY_NAME_2_x;
        } else {
            return QUERY_KEY_NAME_5_x;
        }
    }

    private String getFilteredKeyName () {
        if (this.esVersion.indexOf("2") > -1) {
            return FILTERED_KEY_NAME_2_x;
        } else {
            return FILTERED_KEY_NAME_5_x;
        }
    }

    public Map<String, Object> build () {
        if (this.queryType == EsQueryType.MATCH_ALL) {
            Map<String, Object> innerQuery = new HashMap<String, Object>();
            innerQuery.put(this.getFilteredKeyName(), this.match);
            if (this.filter != null && this.filter.size() > 0) {
                innerQuery.put(FILTER_KEY_NAME, this.filter);
            }
            Map<String, Object> outerQuery = new HashMap<String, Object>();
            outerQuery.put(this.getQueryKeyName(), innerQuery);

            return outerQuery;
        } else if (this.queryType == EsQueryType.MATCH) {
            Map<String, Object> matchQuery = new HashMap<String, Object>();
            matchQuery.put("match", this.match);
            List<Map<String, Object>> matchList = new ArrayList<>();
            matchList.add(matchQuery);
            Map<String, Object> innerQuery = new HashMap<String, Object>();
            innerQuery.put("must", matchList);
            Map<String, Object> outerQuery = new HashMap<String, Object>();
            outerQuery.put("bool", innerQuery);

            return outerQuery;
        }

        return null;
    }
}
