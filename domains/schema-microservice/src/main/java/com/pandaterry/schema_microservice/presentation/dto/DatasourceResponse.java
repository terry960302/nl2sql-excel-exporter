package com.pandaterry.schema_microservice.presentation.dto;

import com.pandaterry.schema_microservice.domain.entity.Datasource;
import com.pandaterry.schema_microservice.domain.enumerated.DatabaseEngineType;
import com.pandaterry.schema_microservice.domain.enumerated.DatabaseType;
import com.pandaterry.schema_microservice.domain.enumerated.EnableStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DatasourceResponse {
    private UUID id;
    private String name;
    private DatabaseType dbType;
    private DatabaseEngineType engineType;
    private String encryptedPassword;
    private String endpoint;
    private boolean sslEnabled;
    private EnableStatus isEnabled;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static DatasourceResponse of(Datasource datasource) {
        return DatasourceResponse.builder()
                .id(datasource.getId())
                .name(datasource.getName())
                .dbType(datasource.getDbType())
                .engineType(datasource.getEngineType())
                .endpoint(datasource.getEndpoint())
                .encryptedPassword(datasource.getPasswordEncrypted())
                .sslEnabled(datasource.isSslEnabled())
                .isEnabled(datasource.getIsEnabled())
                .createdAt(datasource.getCreatedAt())
                .updatedAt(datasource.getUpdatedAt())
                .build();
    }
}