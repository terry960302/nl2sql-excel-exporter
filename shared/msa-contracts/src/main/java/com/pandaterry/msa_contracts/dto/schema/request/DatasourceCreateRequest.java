package com.pandaterry.msa_contracts.dto.schema.request;

import java.util.Map;
import java.util.UUID;

import com.pandaterry.msa_contracts.enums.schema.DatabaseEngineType;
import com.pandaterry.msa_contracts.enums.schema.DatabaseType;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class DatasourceCreateRequest {
    private UUID id;
    private String name;
    private DatabaseType dbType;
    private DatabaseEngineType engineType;

    public static DatasourceCreateRequest of(UUID id, String name, DatabaseType dbType, DatabaseEngineType engineType) {
        return DatasourceCreateRequest.builder()
                .id(id)
                .name(name)
                .dbType(dbType)
                .engineType(engineType)
                .build();
    }
}