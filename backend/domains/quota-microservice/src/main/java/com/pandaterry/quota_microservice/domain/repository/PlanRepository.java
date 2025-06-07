package com.pandaterry.quota_microservice.domain.repository;

import com.pandaterry.quota_microservice.domain.entity.Plan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface PlanRepository extends JpaRepository<Plan, UUID> {
}
