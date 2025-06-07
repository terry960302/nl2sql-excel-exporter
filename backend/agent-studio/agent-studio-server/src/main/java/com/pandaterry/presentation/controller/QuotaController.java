package com.pandaterry.presentation.controller;

import java.util.UUID;

import com.pandaterry.msa_contracts.constants.ApiPath;
import com.pandaterry.msa_contracts.constants.HeaderKeys;
import com.pandaterry.infrastructure.client.QuotaServiceClient;
import com.pandaterry.msa_contracts.dto.quota.request.QuotaUsageRecordRequest;
import com.pandaterry.msa_contracts.dto.quota.response.QuotaOrgResponse;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Header;
import io.micronaut.http.annotation.Post;
import jakarta.inject.Inject;

@Controller("/api/v1/" + ApiPath.Quota.BASE)
public class QuotaController {
    @Inject
    private QuotaServiceClient quotaServiceClient;

    @Get(ApiPath.Quota.ORG_ME_SUFFIX)
    public HttpResponse<QuotaOrgResponse> getMyOrgQuota(@Header(HeaderKeys.ORG_ID) UUID orgId) {
        return HttpResponse.ok(quotaServiceClient.getMyOrgQuota(orgId));
    }

    @Post(ApiPath.Quota.USAGE_SUFFIX)
    public HttpResponse<Void> recordUsage(QuotaUsageRecordRequest request) {
        return HttpResponse.ok(quotaServiceClient.recordUsage(request));
    }
}
