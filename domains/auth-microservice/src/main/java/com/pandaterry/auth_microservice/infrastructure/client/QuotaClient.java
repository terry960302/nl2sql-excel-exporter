package com.pandaterry.auth_microservice.infrastructure.client;

import com.pandaterry.auth_microservice.presentation.dto.QuotaInfo;

public interface QuotaClient {
    QuotaInfo getCurrentQuota(String userId);
}