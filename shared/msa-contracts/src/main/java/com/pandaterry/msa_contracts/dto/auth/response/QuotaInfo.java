package com.pandaterry.msa_contracts.dto.auth.response;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

/**
 * 예시:
 * QuotaInfo info = QuotaInfo.builder()
 *     .totalQuota(100)
 *     .usedQuota(10)
 *     .remainingQuota(90)
 *     .build();
 */
@Getter
@ToString
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class QuotaInfo {
    private final int totalQuota;
    private final int usedQuota;
    private final int remainingQuota;

    public static QuotaInfo of(int totalQuota, int usedQuota, int remainingQuota) {
        return QuotaInfo.builder()
                .totalQuota(totalQuota)
                .usedQuota(usedQuota)
                .remainingQuota(remainingQuota)
                .build();
    }
}