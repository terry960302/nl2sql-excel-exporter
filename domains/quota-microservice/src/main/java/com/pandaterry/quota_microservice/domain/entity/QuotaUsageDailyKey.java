package com.pandaterry.quota_microservice.domain.entity;

import jakarta.persistence.Embeddable;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Objects;
import java.util.UUID;

@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class QuotaUsageDailyKey implements Serializable {
    private UUID orgId;
    private LocalDate dateKey;

    @Override
    public int hashCode() {
        return Objects.hash(orgId, dateKey);
    }

    @Override
    public boolean equals(Object o) {
        if(o == this) return true;
        if(!(o instanceof QuotaUsageDailyKey that)) return false;
        return this.orgId.equals(that.orgId) && this.dateKey.equals(that.dateKey);
    }
}
