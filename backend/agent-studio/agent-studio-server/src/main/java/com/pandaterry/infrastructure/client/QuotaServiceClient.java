package com.pandaterry.infrastructure.client;

import com.pandaterry.msa_contracts.constants.ApiPath;
import com.pandaterry.msa_contracts.constants.HeaderKeys;
import com.pandaterry.msa_contracts.dto.quota.request.QuotaUsageRecordRequest;
import com.pandaterry.msa_contracts.dto.quota.response.QuotaOrgResponse;

import io.micronaut.http.client.HttpClient;
import jakarta.inject.Singleton;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Singleton
public class QuotaServiceClient extends BaseServiceClient {

    private static final String PREFIX = "/api/v1";

    public QuotaServiceClient(HttpClient httpClient, String baseUrl, String agentSecret) {
        super(httpClient, baseUrl, agentSecret);
    }

    // 내 사용량 조회
    public QuotaOrgResponse getMyOrgQuota(UUID orgId) {
        Map<String, String> header = new HashMap<>();
        header.put(HeaderKeys.ORG_ID, orgId.toString());
        return get(PREFIX + ApiPath.Quota.ORG_ME, header, QuotaOrgResponse.class);
    }

    // 사용량 기록
    public Void recordUsage(QuotaUsageRecordRequest request) {
        return post(PREFIX + ApiPath.Quota.USAGE, request, Void.class);
    }
}