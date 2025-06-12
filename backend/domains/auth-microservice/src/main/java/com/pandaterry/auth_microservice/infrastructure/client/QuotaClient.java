package com.pandaterry.auth_microservice.infrastructure.client;

import com.pandaterry.msa_contracts.constants.RoutePath;
import com.pandaterry.msa_contracts.dto.auth.response.QuotaInfo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(
        name = "quota-service",
        url = "${quota.client.base-url}",  // ← 여기를 플레이스홀더로
        path = RoutePath.Quota.BASE
)
public interface QuotaClient {
    @GetMapping
    QuotaInfo getCurrentQuota(String userId);
}