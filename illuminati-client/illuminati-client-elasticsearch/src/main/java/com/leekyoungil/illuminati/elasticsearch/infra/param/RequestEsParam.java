package com.leekyoungil.illuminati.elasticsearch.infra.param;

import com.google.gson.annotations.Expose;
import com.leekyoungil.illuminati.common.constant.IlluminatiConstant;
import com.leekyoungil.illuminati.elasticsearch.infra.enums.EsOrderType;
import com.leekyoungil.illuminati.elasticsearch.infra.param.query.EsQuery;

import java.util.List;

public class RequestEsParam {

    @Expose
    private int size = 10;
    @Expose
    private int from = 0;
    @Expose
    private final EsQuery query;
    @Expose
    private RequestEsSortParam sort;
    @Expose
    private final List<String> source;

    public RequestEsParam (EsQuery query, List<String> source) {
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

    public RequestEsParam setSort (EsOrderType orderType, String key) {
        this.sort = new RequestEsSortParam(orderType, key);
        return this;
    }

    public String build () {
        return IlluminatiConstant.ILLUMINATI_GSON_OBJ.toJson(this);
    }
}
