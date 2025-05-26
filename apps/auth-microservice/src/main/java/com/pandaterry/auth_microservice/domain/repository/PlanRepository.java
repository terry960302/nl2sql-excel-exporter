package com.pandaterry.auth_microservice.domain.repository;

import com.pandaterry.auth_microservice.domain.entity.Plan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface PlanRepository extends JpaRepository<Plan, UUID> {
    boolean existsByName(String name);

    Optional<Plan> findByName(String name);
}