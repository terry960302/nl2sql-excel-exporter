package com.pandaterry.domain.model.datasource;

import com.pandaterry.msa_contracts.enums.schema.DatabaseEngineType;
import io.micronaut.serde.annotation.Serdeable;

import java.util.UUID;


@Serdeable
public class DatasourceSession {
    private final UUID id;
    private final UUID datasourceId;
    private final String jdbcUrl;
    private final String username;
    private final String password;
    private final DatabaseEngineType type;

    private DatasourceSession(String jdbcUrl, String username, String password, DatabaseEngineType type) {
        this.id = UUID.randomUUID();
        this.datasourceId = UUID.randomUUID();
        this.jdbcUrl = jdbcUrl;
        this.username = username;
        this.password = password;
        this.type = type;
    }

    public static DatasourceSession create(String jdbcUrl, String username, String password, DatabaseEngineType type) {
        return new DatasourceSession(jdbcUrl, username, password, type);
    }

    // Getters
//    public UUID getId() {
//        return id;
//    }

    public UUID getDatasourceId() {
        return datasourceId;
    }

    public String getJdbcUrl() {
        return jdbcUrl;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public DatabaseEngineType getType() {
        return type;
    }
}