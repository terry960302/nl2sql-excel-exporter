package com.pandaterry.quota_microservice.domain.entity;

import jakarta.persistence.Embeddable;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.UUID;

@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class QuotaUsageDailyKey implements Serializable {
    private UUID orgId;
    private LocalDate dateKey;
}
