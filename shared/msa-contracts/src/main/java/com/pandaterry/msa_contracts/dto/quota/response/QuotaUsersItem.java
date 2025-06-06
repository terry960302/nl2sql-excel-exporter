package com.pandaterry.msa_contracts.dto.quota.response;

import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

@Getter
@Builder
public class QuotaUsersItem {
    private final UUID orgId;
    private final int monthlyQuota;
    private final long monthCount;
    private final int remainingQuota;
}
