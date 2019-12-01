package me.phoboslabs.illuminati.elasticsearch.infra.properties;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import me.phoboslabs.illuminati.common.properties.IlluminatiBaseProperties;
import me.phoboslabs.illuminati.common.util.StringObjectUtils;
import me.phoboslabs.illuminati.elasticsearch.infra.model.EsInfo;

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

    public String getHost () throws Exception {
        if (this.elasticsearchInfo != null) {
            return this.elasticsearchInfo.getHost();
        }

        throw new Exception("elasticsearch host info must not be null.");
    }

    public int getPort () throws Exception {
        if (this.elasticsearchInfo != null) {
            return this.elasticsearchInfo.getPort();
        }
        throw new Exception("elasticsearch port info must not be null.");
    }

    public boolean isValid () {
        return this.elasticsearchInfo != null && StringObjectUtils.isValid(this.elasticsearchInfo.getHost())
                && this.elasticsearchInfo.getPort() > 0;
    }
}
