package me.phoboslabs.illuminati.elasticsearch.infra.param.query;

import com.google.gson.annotations.Expose;

import java.util.Map;

public class EsQuery {

    @Expose
    private final Map<String, Object> query;

    public EsQuery (Map<String, Object> query) {
        this.query = query;
    }

    public Map<String, Object> getQuery () {
        return this.query;
    }
}
