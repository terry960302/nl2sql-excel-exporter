package com.pandaterry.quota_microservice.domain.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Table(name = "plans")
@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class Plan {

    @Id
    private UUID id;

    @Column(name = "monthly_quota", nullable = false)
    private int monthlyQuota;

    @Column(name = "rate_limit_rps", nullable = false)
    private int rateLimitRps;

    public static Plan create(UUID id, int monthlyQuota, int rateLimitRps) {
        return Plan.builder()
                .id(id)
                .monthlyQuota(monthlyQuota)
                .rateLimitRps(rateLimitRps)
                .build();
    }
}
