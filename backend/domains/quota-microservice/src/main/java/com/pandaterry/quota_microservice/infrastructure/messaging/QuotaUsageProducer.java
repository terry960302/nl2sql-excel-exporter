package com.pandaterry.quota_microservice.infrastructure.messaging;


import com.pandaterry.msa_contracts.event.QuotaUsageEvent;

public interface QuotaUsageProducer {
    void sendQuotaUsage(QuotaUsageEvent event);
}
