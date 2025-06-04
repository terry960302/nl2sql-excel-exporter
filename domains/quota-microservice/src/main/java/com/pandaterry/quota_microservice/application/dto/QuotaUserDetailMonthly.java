package com.pandaterry.quota_microservice.application.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class QuotaUserDetailMonthly {
    private final String monthKey;
    private final long count;
    private final int monthlyQuota;
}
