package com.pandaterry.auth_microservice.domain.repository;

import com.pandaterry.auth_microservice.domain.entity.Organization;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface OrganizationRepository extends JpaRepository<Organization, UUID> {
    boolean existsByName(String name);

    Optional<Organization> findByName(String name);
}