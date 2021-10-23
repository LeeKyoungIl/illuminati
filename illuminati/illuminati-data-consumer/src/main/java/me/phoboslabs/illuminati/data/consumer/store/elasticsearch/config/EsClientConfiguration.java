package me.phoboslabs.illuminati.data.consumer.store.elasticsearch.config;

import me.phoboslabs.illuminati.common.http.IlluminatiHttpClient;
import me.phoboslabs.illuminati.elasticsearch.infra.ESclientImpl;
import me.phoboslabs.illuminati.elasticsearch.infra.EsClient;
import me.phoboslabs.illuminati.elasticsearch.infra.model.EsInfo;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "elasticsearch-info")
public class EsClientConfiguration {

    public EsClientConfiguration() {
    }

    @NestedConfigurationProperty
    private EsInfo master;

    public void setMaster(EsInfo master) {
        this.master = master;
    }

    @Bean
    public EsClient esClient () {
        return new ESclientImpl(new IlluminatiHttpClient(), this.master.getHost(), this.master.getPort());
    }
}
