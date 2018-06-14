package com.leekyoungil.illuminati.elasticsearch.infra.properties;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.leekyoungil.illuminati.common.properties.IlluminatiBaseProperties;
import com.leekyoungil.illuminati.common.util.StringObjectUtils;
import com.leekyoungil.illuminati.elasticsearch.infra.model.EsInfo;

import java.util.Properties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class EsClientProperties extends IlluminatiBaseProperties {

    private EsInfo elasticsearchInfo;

    public EsClientProperties() {
        super();
    }

    public EsClientProperties(final Properties prop) {
        super(prop);
    }

    public EsInfo getElasticsearchInfo() {
        return this.elasticsearchInfo;
    }

    public String getHost () {
        if (this.elasticsearchInfo != null) {
            return this.elasticsearchInfo.getHost();
        } else {
            return null;
        }
    }

    public int getPort () {
        if (this.elasticsearchInfo != null) {
            return this.elasticsearchInfo.getPort();
        } else {
            return 0;
        }
    }

    public boolean isValid () {
        if (this.elasticsearchInfo != null && StringObjectUtils.isValid(this.elasticsearchInfo.getHost()) == true
                && this.elasticsearchInfo.getPort() > 0) {
            return true;
        } else {
            return false;
        }
    }
}
