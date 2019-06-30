package com.leekyoungil.illuminati.elasticsearch.infra.param;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.leekyoungil.illuminati.common.constant.IlluminatiConstant;
import com.leekyoungil.illuminati.elasticsearch.infra.enums.EsOrderType;
import com.leekyoungil.illuminati.elasticsearch.infra.param.sort.EsSort;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RequestEsParam {

    @Expose
    private int size = 10;
    @Expose
    private int from = 0;
    @Expose
    private Map<String, Object> query;
    @Expose
    private Map<String, String> sort = new HashMap<String, String>();
    @Expose @SerializedName("_source")
    private List<String> source;
    @Expose
    private Map<String, Object> aggs;

    public static RequestEsParam Builder () {
        return new RequestEsParam();
    }
    public static RequestEsParam Builder (Map<String, Object> query) {
        return new RequestEsParam(query);
    }
    public static RequestEsParam Builder (Map<String, Object> query, List<String> source) {
        return new RequestEsParam(query, source);
    }

    private RequestEsParam () {

    }

    private RequestEsParam (Map<String, Object> query) {
        this.query = query;
    }

    private RequestEsParam (Map<String, Object> query, List<String> source) {
        this.query = query;
        this.source = source;
    }

    public RequestEsParam setSize (int size) {
        this.size = size;
        return this;
    }

    public RequestEsParam setFrom (int from) {
        this.from = from;
        return this;
    }

    public RequestEsParam setSource (List<String> source) {
        this.source = source;
        return this;
    }

    public RequestEsParam setSort (Map<String, String> sort) {
        this.sort = sort;
        return this;
    }

    public RequestEsParam setGroupBy (Map<String, Object> groupBy) {
        this.aggs = groupBy;
        return this;
    }

    public RequestEsParam setQuery (Map<String, Object> query) {
        this.query = query;
        return this;
    }

    private void resetFieldsWithoutAggregation () {
        this.size = 0;
        this.from = 0;

        if (this.source != null) {
            this.source.clear();
        } else {
            this.source = new ArrayList<String>();
        }
        this.resetSort();
    }

    private void resetAggregationField () {
        this.aggs = new HashMap<String, Object>();
    }

    private void resetSort () {
        if (this.sort != null) {
            this.sort.clear();
        } else {
            this.sort = new HashMap<String, String>();
        }
    }

    public String build () {
        if (this.aggs != null) {
            this.resetFieldsWithoutAggregation();
        } else {
            this.resetAggregationField();
        }
        return IlluminatiConstant.ILLUMINATI_GSON_OBJ.toJson(this);
    }
}
