package com.pandaterry.quota_microservice.domain.repository;

import com.pandaterry.quota_microservice.domain.entity.QuotaUsageDaily;
import com.pandaterry.quota_microservice.domain.entity.QuotaUsageDailyKey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Repository
public interface QuotaUsageDailyRepository extends JpaRepository<QuotaUsageDaily, QuotaUsageDailyKey> {
    List<QuotaUsageDaily> findByIdOrgIdAndIdDateKeyBetweenOrderByIdDateKeyAsc(UUID orgId, LocalDate start, LocalDate end);
}
