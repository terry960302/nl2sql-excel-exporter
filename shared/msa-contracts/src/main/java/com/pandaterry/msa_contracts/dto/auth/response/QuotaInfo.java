package com.pandaterry.msa_contracts.dto.auth.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QuotaInfo {
    private int totalQuota;
    private int usedQuota;
    private int remainingQuota;

    public static QuotaInfo of(int totalQuota, int usedQuota, int remainingQuota) {
        return QuotaInfo.builder()
                .totalQuota(totalQuota)
                .usedQuota(usedQuota)
                .remainingQuota(remainingQuota)
                .build();
    }
}