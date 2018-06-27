package com.leekyoungil.illuminati.elasticsearch.infra.param.source;

import com.google.gson.annotations.Expose;
import com.leekyoungil.illuminati.common.util.StringObjectUtils;
import org.apache.commons.collections.CollectionUtils;

import java.util.List;

public class EsSourceBuilder {

    @Expose
    private final EsSource esSource = new EsSource();

    public static EsSourceBuilder Builder () {
        return new EsSourceBuilder();
    }

    private EsSourceBuilder () {

    }

    public EsSourceBuilder setSource (String column) {
        if (StringObjectUtils.isValid(column) == false) {
            return this;
        }
        this.esSource.setSource(column);
        return this;
    }

    public EsSourceBuilder setSource (List<String> sources) {
        if (CollectionUtils.isNotEmpty(sources)) {
            for (String source : sources) {
                this.setSource(source);
            }
        }
        return this;
    }

    public List<String> build () {
        return this.esSource.getSource();
    }
}
