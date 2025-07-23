package com.pandaterry.query_microservice.infrastructure.messaging;

import com.pandaterry.msa_contracts.dto.quota.request.QuotaUsageRecordRequest;
import com.pandaterry.msa_contracts.event.QuotaUsageEvent;

public interface QuotaUsageProducer {
    void sendQuotaUsage(QuotaUsageEvent event);
}
