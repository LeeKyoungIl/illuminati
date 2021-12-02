package me.phoboslabs.illuminati.elasticsearch.infra;

import me.phoboslabs.illuminati.elasticsearch.infra.properties.EsConnectionProperties;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;
import org.yaml.snakeyaml.introspector.BeanAccess;

import java.io.InputStream;

public class IlluminatiEsConnector {

    private EsConnectionProperties esConnectionProperties;
    private EsConnectionProperties.ElasticsearchInfo elasticsearchInfo;

    public IlluminatiEsConnector() {
        final String profiles = System.getProperty("spring.profiles.active");
        final Yaml yaml = new Yaml(new Constructor(EsConnectionProperties.class));
        yaml.setBeanAccess(BeanAccess.FIELD);
        final InputStream inputStream = this.getClass()
                .getClassLoader()
                .getResourceAsStream("config/elasticsearch/elasticsearch-"+profiles+".yml");

        if (inputStream == null) {
            throw new IllegalArgumentException("config/elasticsearch/elasticsearch-{phase}.yml not exists.");
        }

        this.esConnectionProperties = yaml.load(inputStream);
        this.elasticsearchInfo = this.esConnectionProperties.getElasticsearchInfo();
    }

    public String getHost() {
        return this.elasticsearchInfo.getHost();
    }

    public int getPort() {
        return this.elasticsearchInfo.getPort();
    }
}
