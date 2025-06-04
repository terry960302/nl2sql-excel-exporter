package com.pandaterry.quota_microservice.application.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class QuotaMeResponse {
    private final long todayCount;
    private final long monthCount;
    private final int monthlyQuota;
    private final int remainingQuota;
}
