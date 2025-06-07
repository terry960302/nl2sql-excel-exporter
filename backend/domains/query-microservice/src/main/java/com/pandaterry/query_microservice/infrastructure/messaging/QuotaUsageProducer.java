package com.pandaterry.query_microservice.infrastructure.messaging;

import com.pandaterry.msa_contracts.dto.quota.request.QuotaUsageRecordRequest;

public interface QuotaUsageProducer {
    void sendQuotaUsage(QuotaUsageRecordRequest request);
}
