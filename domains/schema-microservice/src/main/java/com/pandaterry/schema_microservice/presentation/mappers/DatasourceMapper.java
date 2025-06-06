package com.pandaterry.schema_microservice.presentation.mappers;

import com.pandaterry.msa_contracts.dto.schema.request.DatasourceCreateRequest;
import com.pandaterry.msa_contracts.dto.schema.request.DatasourceUpdateRequest;
import com.pandaterry.msa_contracts.dto.schema.response.DatasourceResponse;
import com.pandaterry.msa_contracts.enums.schema.DatabaseEngineType;
import com.pandaterry.msa_contracts.enums.schema.DatabaseType;
import com.pandaterry.schema_microservice.domain.entity.Datasource;

import java.util.Map;
import java.util.UUID;

public class DatasourceMapper {
    public static Datasource toEntity(UUID orgId, UUID userId, UUID agentId, DatasourceCreateRequest request) {
        return Datasource.create(orgId, request.getName(), request.getDbType(), request.getEngineType(), userId, agentId);
    }

    public static DatasourceResponse toResponse(Datasource datasource) {
        return DatasourceResponse.builder()
                .id(datasource.getId())
                .name(datasource.getName())
                .dbType(datasource.getDbType())
                .engineType(datasource.getEngineType())
                .isEnabled(datasource.getIsEnabled())
                .createdAt(datasource.getCreatedAt())
                .updatedAt(datasource.getUpdatedAt())
                .build();
    }

    public static DatasourceUpdateRequest of(String name, DatabaseType dbType, DatabaseEngineType engineType) {
        return DatasourceUpdateRequest.builder()
                .name(name)
                .dbType(dbType)
                .engineType(engineType)
                .build();
    }
}
