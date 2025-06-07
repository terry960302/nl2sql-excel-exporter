package com.pandaterry.quota_microservice.domain.repository;

import com.pandaterry.quota_microservice.domain.entity.QuotaUsageMonthly;
import com.pandaterry.quota_microservice.domain.entity.QuotaUsageMonthlyKey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface QuotaUsageMonthlyRepository extends JpaRepository<QuotaUsageMonthly, QuotaUsageMonthlyKey> {
}
