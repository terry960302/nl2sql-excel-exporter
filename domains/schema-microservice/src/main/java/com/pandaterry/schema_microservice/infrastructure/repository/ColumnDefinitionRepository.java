package com.pandaterry.schema_microservice.infrastructure.repository;

import com.pandaterry.schema_microservice.domain.entity.ColumnDefinition;
import com.pandaterry.msa_contracts.enums.schema.EnableStatus;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ColumnDefinitionRepository extends JpaRepository<ColumnDefinition, UUID> {
    List<ColumnDefinition> findByTableIdAndIsEnabled(UUID tableId, EnableStatus isEnabled);
}