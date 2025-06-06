package com.pandaterry.msa_contracts.dto.quota.response;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class QuotaUserDetailResponse {
    private final List<QuotaUserDetailDaily> dailyUsage;
    private final QuotaUserDetailMonthly monthlyUsage;
}
