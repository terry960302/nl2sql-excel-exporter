package com.pandaterry.infrastructure.client;

import com.pandaterry.msa_contracts.constants.HeaderKeys;
import com.pandaterry.msa_contracts.dto.quota.request.QuotaUsageRecordRequest;
import com.pandaterry.msa_contracts.dto.quota.response.QuotaOrgResponse;
import io.micronaut.http.annotation.Header;

public interface QuotaClient {
    QuotaOrgResponse getMyOrgQuota(@Header(HeaderKeys.AUTHORIZATION) String authorization);
    Void recordUsage(@Header(HeaderKeys.AUTHORIZATION) String authorization, QuotaUsageRecordRequest request);
}
