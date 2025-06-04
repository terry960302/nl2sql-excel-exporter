package com.pandaterry.quota_microservice.application.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class QuotaUserDetailResponse {
    private final List<QuotaUserDetailDaily> dailyUsage;
    private final QuotaUserDetailMonthly monthlyUsage;
}
