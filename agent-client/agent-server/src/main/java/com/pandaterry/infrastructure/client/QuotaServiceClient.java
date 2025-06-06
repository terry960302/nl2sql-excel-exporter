package com.pandaterry.infrastructure.client;

import com.pandaterry.msa_contracts.constants.HeaderKeys;
import com.pandaterry.msa_contracts.dto.quota.request.QuotaUsageRecordRequest;
import com.pandaterry.msa_contracts.dto.quota.response.QuotaOrgDetailResponse;
import com.pandaterry.msa_contracts.dto.quota.response.QuotaOrgResponse;
import com.pandaterry.msa_contracts.dto.quota.response.QuotaOrgsPageResponse;

import io.micronaut.http.client.HttpClient;
import jakarta.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Singleton
public class QuotaServiceClient extends BaseServiceClient {

    public QuotaServiceClient(HttpClient httpClient, String baseUrl, String agentSecret) {
        super(httpClient, baseUrl, agentSecret);
    }

    // 내 사용량 조회
    public QuotaOrgResponse getMyOrgQuota(UUID orgId) {
        Map<String, String> header = new HashMap<>();
        header.put(HeaderKeys.ORG_ID, orgId.toString());
        return get("/api/quota/me", header, QuotaOrgResponse.class);
    }

    // 사용량 기록
    public Void recordUsage(QuotaUsageRecordRequest request) {
        return post("/api/quota/usage", request, Void.class);
    }
}