package com.leekyoungil.illuminati.elasticsearch.infra.param.source;

import com.google.gson.annotations.Expose;

import java.util.ArrayList;
import java.util.List;

public class EsSource {

    @Expose
    private List<String> source = new ArrayList<String>();

    public EsSource setSource (String columnName) {
        this.source.add(columnName);
        return this;
    }

    public List<String> build () {
        return this.source.size() > 0 ? this.source : null;
    }
}
