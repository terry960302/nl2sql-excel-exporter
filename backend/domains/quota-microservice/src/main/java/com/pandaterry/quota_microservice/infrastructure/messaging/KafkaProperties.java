package com.pandaterry.quota_microservice.infrastructure.messaging;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@ConfigurationProperties(prefix = "app.kafka")
@Component
public class KafkaProperties {

    private Topics topics = new Topics();

    @Data
    public static class Topics {
        private String quotaUsage;
        private String userEvents;
    }
}
