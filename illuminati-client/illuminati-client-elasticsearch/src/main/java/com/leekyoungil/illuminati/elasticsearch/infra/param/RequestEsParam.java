package com.leekyoungil.illuminati.elasticsearch.infra.param;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.leekyoungil.illuminati.common.constant.IlluminatiConstant;
import com.leekyoungil.illuminati.elasticsearch.infra.enums.EsOrderType;
import com.leekyoungil.illuminati.elasticsearch.infra.param.sort.EsSort;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RequestEsParam {

    @Expose
    private int size = 10;
    @Expose
    private int from = 0;
    @Expose
    private final Map<String, Object> query;
    @Expose
    private Map<String, String> sort;
    @Expose @SerializedName("_source")
    private final List<String> source;

    public RequestEsParam (Map<String, Object> query, List<String> source) {
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

    public RequestEsParam setSort (Map<String, String> sort) {
        this.sort = sort;
        return this;
    }

    public String build () {
        return IlluminatiConstant.ILLUMINATI_GSON_OBJ.toJson(this);
    }
}
