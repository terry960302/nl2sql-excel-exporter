package com.pandaterry.query_microservice.infrastructure.messaging;

import com.pandaterry.msa_contracts.dto.quota.request.QuotaUsageRecordRequest;
import com.pandaterry.msa_contracts.event.QuotaUsageEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RabbitQuotaUsageProducer implements QuotaUsageProducer {

    private final RabbitTemplate rabbitTemplate;

    @Value("${quota.usage.queue:quota-usage}")
    private String queue;

    @Override
    public void sendQuotaUsage(QuotaUsageEvent request) {
        rabbitTemplate.convertAndSend(queue, request);
    }
}
