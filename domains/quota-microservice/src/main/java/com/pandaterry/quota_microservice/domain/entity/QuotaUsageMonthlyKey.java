package com.pandaterry.quota_microservice.domain.entity;

import jakarta.persistence.Embeddable;
import lombok.*;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class QuotaUsageMonthlyKey implements Serializable {
    private UUID orgId;
    private String monthKey;

    @Override
    public int hashCode() {
        return Objects.hash(orgId, monthKey);
    }

    @Override
    public boolean equals(Object o) {
        if(o == this) return true;
        if(!(o instanceof QuotaUsageMonthlyKey that)) return false;
        return this.orgId.equals(that.orgId) && this.monthKey.equals(that.monthKey);
    }
}
