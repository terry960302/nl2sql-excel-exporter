package com.pandaterry.auth_microservice.infrastructure.client;

import com.pandaterry.msa_contracts.dto.auth.response.QuotaInfo;

public interface QuotaClient {
    QuotaInfo getCurrentQuota(String userId);
}