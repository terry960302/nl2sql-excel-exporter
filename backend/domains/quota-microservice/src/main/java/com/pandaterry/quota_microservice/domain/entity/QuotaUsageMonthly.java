package com.pandaterry.quota_microservice.domain.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "quota_usage_monthly")
@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class QuotaUsageMonthly {

    @EmbeddedId
    private QuotaUsageMonthlyKey id;

    @Column(nullable = false)
    private long count;

    public static QuotaUsageMonthly create(UUID orgId, String monthKey, long count) {
        return QuotaUsageMonthly.builder()
                .id(QuotaUsageMonthlyKey.builder().orgId(orgId).monthKey(monthKey).build())
                .count(count)
                .build();
    }

    public void increase(long increment) {
        this.count += increment;
    }
}
