package com.pandaterry.schema_microservice.presentation.dto;

import java.util.Map;
import java.util.UUID;

import com.pandaterry.schema_microservice.domain.entity.Datasource;
import com.pandaterry.schema_microservice.domain.enumerated.DatabaseEngineType;
import com.pandaterry.schema_microservice.domain.enumerated.DatabaseType;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class DatasourceCreateRequest {
        private String name;
        private DatabaseType dbType;
        private DatabaseEngineType engineType;
        private String endpoint;
        private String username;
        private String password;
        private boolean sslEnabled;
        private Map<String, Object> options;

        public Datasource toEntity(UUID orgId, String encryptedPassword) {
                return Datasource.create(orgId, name, dbType, engineType, endpoint, username, encryptedPassword, sslEnabled, options);
        }

        public static DatasourceCreateRequest of(String name, DatabaseType dbType, DatabaseEngineType engineType,
                        String endpoint, String username, String password, boolean sslEnabled,
                        Map<String, Object> options) {
                return DatasourceCreateRequest.builder()
                                .name(name)
                                .dbType(dbType)
                                .engineType(engineType)
                                .endpoint(endpoint)
                                .username(username)
                                .password(password)
                                .sslEnabled(sslEnabled)
                                .options(options)
                                .build();
        }
}