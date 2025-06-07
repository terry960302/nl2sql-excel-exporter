package com.pandaterry.schema_microservice.infrastructure.repository;

import com.pandaterry.schema_microservice.domain.entity.Datasource;
import com.pandaterry.msa_contracts.enums.schema.EnableStatus;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface DatasourceRepository extends JpaRepository<Datasource, UUID> {
    List<Datasource> findByOrgIdAndIsEnabled(UUID orgId, EnableStatus isEnabled);
}