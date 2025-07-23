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
    private String alias;
    private String name;
    private DatabaseType dbType;
    private DatabaseEngineType engineType;
    private String username;
    private String password;
    private String description;

    public static DatasourceCreateRequest of(UUID id,
                                             String alias,
                                             String name,
                                             String username,
                                             String password,
                                             DatabaseType dbType,
                                             DatabaseEngineType engineType,
                                             String description) {
        return DatasourceCreateRequest.builder()
                .id(id)
                .name(name)
                .alias(alias)
                .username(username)
                .password(password)
                .dbType(dbType)
                .engineType(engineType)
                .description(description)
                .build();
    }
}