package com.pandaterry.schema_microservice.infrastructure.repository;

import com.pandaterry.schema_microservice.domain.entity.Schema;
import com.pandaterry.schema_microservice.domain.enumerated.EnableStatus;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface SchemaRepository extends JpaRepository<Schema, UUID> {
    List<Schema> findByDatasourceIdAndIsEnabled(UUID datasourceId, EnableStatus isEnabled);
}