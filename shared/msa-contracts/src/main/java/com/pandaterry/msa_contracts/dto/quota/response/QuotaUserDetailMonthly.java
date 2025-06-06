package com.pandaterry.msa_contracts.dto.quota.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class QuotaUserDetailMonthly {
    private final String monthKey;
    private final long count;
    private final int monthlyQuota;
}
