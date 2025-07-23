package com.pandaterry.presentation.controller;

import com.pandaterry.infrastructure.client.DefaultQuotaClient;
import com.pandaterry.msa_contracts.constants.RoutePath;
import com.pandaterry.msa_contracts.constants.HeaderKeys;
import com.pandaterry.msa_contracts.dto.quota.request.QuotaUsageRecordRequest;
import com.pandaterry.msa_contracts.dto.quota.response.QuotaOrgResponse;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Header;
import io.micronaut.http.annotation.Post;
import io.micronaut.scheduling.TaskExecutors;
import io.micronaut.scheduling.annotation.ExecuteOn;
import jakarta.inject.Inject;

@Controller(RoutePath.Quota.BASE)
public class QuotaController {
    @Inject
    private DefaultQuotaClient quotaClient;

    @Get(RoutePath.Quota.ORG_ME_SUFFIX)
    public HttpResponse<QuotaOrgResponse> getMyOrgQuota(@Header(HeaderKeys.AUTHORIZATION) String authorization) {
        return HttpResponse.ok(quotaClient.getMyOrgQuota(authorization));
    }

    @Post(RoutePath.Quota.USAGE_SUFFIX)
    public HttpResponse<Void> recordUsage(@Header(HeaderKeys.AUTHORIZATION) String authorization, QuotaUsageRecordRequest request) {
        return HttpResponse.ok(quotaClient.recordUsage(authorization, request));
    }
}
