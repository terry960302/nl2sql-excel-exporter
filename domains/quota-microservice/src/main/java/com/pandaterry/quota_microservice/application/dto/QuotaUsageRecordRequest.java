package com.pandaterry.quota_microservice.application.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

@Getter
@Builder
public class QuotaUsageRecordRequest {
    private final UUID orgId;
    private final long increment;
}
