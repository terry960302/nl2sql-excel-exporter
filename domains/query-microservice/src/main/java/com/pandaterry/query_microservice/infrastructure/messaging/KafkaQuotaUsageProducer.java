package com.pandaterry.query_microservice.infrastructure.messaging;

import com.pandaterry.msa_contracts.dto.quota.request.QuotaUsageRecordRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class KafkaQuotaUsageProducer implements QuotaUsageProducer {

    private final KafkaTemplate<String, QuotaUsageRecordRequest> kafkaTemplate;

    @Value("${quota.usage.topic:quota-usage}")
    private String topic;

    @Override
    public void sendQuotaUsage(QuotaUsageRecordRequest request) {
        kafkaTemplate.send(topic, request);
    }
}
