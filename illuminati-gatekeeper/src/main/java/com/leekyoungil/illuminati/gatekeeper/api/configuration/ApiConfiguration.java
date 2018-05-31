package com.leekyoungil.illuminati.gatekeeper.api.configuration;

import com.leekyoungil.illuminati.common.http.IlluminatiHttpClient;
import com.leekyoungil.illuminati.common.util.PropertiesUtil;
import com.leekyoungil.illuminati.elasticsearch.infra.ESclientImpl;
import com.leekyoungil.illuminati.elasticsearch.infra.EsClient;
import com.leekyoungil.illuminati.elasticsearch.infra.properties.EsClientProperties;
import com.leekyoungil.illuminati.gatekeeper.api.service.HostInfoService;
import com.leekyoungil.illuminati.gatekeeper.api.service.JvmInfoApiService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ApiConfiguration {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Bean
    public EsClient getEsClient () {
        EsClientProperties esInfo = PropertiesUtil.getIlluminatiProperties(EsClientProperties.class, "elasticsearch/elasticsearch");
        if (esInfo.isValid() == true) {
            return new ESclientImpl(new IlluminatiHttpClient(), esInfo.getHost(), esInfo.getPort());
        } else {
            this.logger.error("failed to generate Elasticsearch client.");
            return null;
        }
    }

    @Bean
    public JvmInfoApiService getJvmInfoApiService () {
        return new JvmInfoApiService(this.getEsClient());
    }

    @Bean
    public HostInfoService getHostInfoService () {
        return new HostInfoService(this.getEsClient());
    }
}
