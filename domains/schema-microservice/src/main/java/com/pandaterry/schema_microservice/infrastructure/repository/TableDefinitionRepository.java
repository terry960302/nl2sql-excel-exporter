package com.pandaterry.schema_microservice.infrastructure.repository;

import com.pandaterry.schema_microservice.domain.entity.TableDefinition;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface TableDefinitionRepository extends JpaRepository<TableDefinition, UUID> {
    List<TableDefinition> findBySchemaId(UUID schemaId);
}