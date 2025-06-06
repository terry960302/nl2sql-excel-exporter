package com.pandaterry.msa_contracts.dto.quota.request;

import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

@Getter
@Builder
public class QuotaUsageRecordRequest {
    private final UUID orgId;
    private final long increment;
}
