package com.pandaterry.msa_contracts.dto.quota.request;

import lombok.*;

import java.util.UUID;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class QuotaUsageRecordRequest {
    private UUID orgId;
    private long increment;
}
