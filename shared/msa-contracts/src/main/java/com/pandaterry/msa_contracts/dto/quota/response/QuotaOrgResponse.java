package com.pandaterry.msa_contracts.dto.quota.response;

import lombok.*;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor
public class QuotaOrgResponse {
    private long todayCount;
    private long monthCount;
    private int monthlyQuota;
    private int remainingQuota;
}
