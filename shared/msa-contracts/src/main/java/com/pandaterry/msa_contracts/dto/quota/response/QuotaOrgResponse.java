package com.pandaterry.msa_contracts.dto.quota.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class QuotaOrgResponse {
    private final long todayCount;
    private final long monthCount;
    private final int monthlyQuota;
    private final int remainingQuota;
}
