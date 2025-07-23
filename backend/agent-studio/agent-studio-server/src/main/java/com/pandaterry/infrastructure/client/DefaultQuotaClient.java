package com.pandaterry.infrastructure.client;

import com.pandaterry.msa_contracts.constants.RoutePath;
import com.pandaterry.msa_contracts.dto.quota.request.QuotaUsageRecordRequest;
import com.pandaterry.msa_contracts.dto.quota.response.QuotaOrgResponse;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Post;
import io.micronaut.http.client.annotation.Client;

@Client(id = "gateway", path = RoutePath.Quota.BASE)
public interface DefaultQuotaClient extends QuotaClient {
    @Get(RoutePath.Quota.ORG_ME_SUFFIX)
    @Override
    QuotaOrgResponse getMyOrgQuota(String authorization);

    @Post(RoutePath.Quota.USAGE_SUFFIX)
    @Override
    Void recordUsage(String authorization, QuotaUsageRecordRequest request);
}
