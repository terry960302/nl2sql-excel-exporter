package com.pandaterry.schema_microservice.infrastructure.repository;

import com.pandaterry.schema_microservice.domain.entity.Alias;
import com.pandaterry.schema_microservice.domain.enumerated.EnableStatus;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface AliasRepository extends JpaRepository<Alias, UUID> {
    List<Alias> findByColumnIdAndIsEnabled(UUID columnId, EnableStatus isEnabled);

    long countByColumnId(UUID columnId);
}