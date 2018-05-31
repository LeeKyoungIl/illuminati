package com.leekyoungil.illuminati.elasticsearch.infra.param;

import com.google.gson.annotations.Expose;

import java.util.ArrayList;
import java.util.List;

public class RequestEsSourceParam {

    @Expose
    private List<String> source = new ArrayList<String>();

    public RequestEsSourceParam setSource (String columnName) {
        this.source.add(columnName);
        return this;
    }

    public List<String> build () {
        return this.source.size() > 0 ? this.source : null;
    }
}
