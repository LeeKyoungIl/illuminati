package me.phoboslabs.illuminati.data.consumer.store.mongodb.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "elasticsearch-info")
public class MongodbClientConfiguration {
}
