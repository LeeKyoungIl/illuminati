package me.phoboslabs.illuminati.esconsumer.config;

import me.phoboslabs.illuminati.common.http.IlluminatiHttpClient;
import me.phoboslabs.illuminati.elasticsearch.infra.ESclientImpl;
import me.phoboslabs.illuminati.elasticsearch.infra.EsClient;
import me.phoboslabs.illuminati.elasticsearch.infra.model.EsInfo;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Data
@NoArgsConstructor
@ConfigurationProperties(prefix = "elasticsearchInfo")
public class EsClientConfig {

    @NestedConfigurationProperty
    private EsInfo master;

    @Bean
    public EsClient esClient () {
        return new ESclientImpl(new IlluminatiHttpClient(), this.master.getHost(), this.master.getPort());
    }
}
