package com.pandaterry.quota_microservice.domain.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "quota_usage_daily")
@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class QuotaUsageDaily {

    @EmbeddedId
    private QuotaUsageDailyKey id;

    @Column(nullable = false)
    private long count;

    public static QuotaUsageDaily create(UUID orgId, java.time.LocalDate dateKey, long count) {
        return QuotaUsageDaily.builder()
                .id(QuotaUsageDailyKey.builder().orgId(orgId).dateKey(dateKey).build())
                .count(count)
                .build();
    }

    public void increase(long increment) {
        this.count += increment;
    }
}
