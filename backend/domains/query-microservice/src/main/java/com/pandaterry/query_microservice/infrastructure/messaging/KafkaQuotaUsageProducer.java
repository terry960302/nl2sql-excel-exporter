package com.pandaterry.query_microservice.infrastructure.messaging;

import com.pandaterry.msa_contracts.dto.quota.request.QuotaUsageRecordRequest;
import com.pandaterry.msa_contracts.event.QuotaUsageEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class KafkaQuotaUsageProducer implements QuotaUsageProducer {

    private final KafkaTemplate<String, QuotaUsageEvent> kafkaTemplate;

    @Value("${quota.usage.topic:quota-usage}")
    private String topic;

    @Override
    public void sendQuotaUsage(QuotaUsageEvent event) {
        kafkaTemplate.send(topic, event);
    }
}
