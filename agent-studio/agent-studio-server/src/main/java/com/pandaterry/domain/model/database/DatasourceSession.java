package com.pandaterry.domain.model.database;

import com.pandaterry.domain.enums.DatabaseType;

import java.util.UUID;


public class DatasourceSession {
    private final UUID id;
    private final UUID datasourceId;
    private final String jdbcUrl;
    private final String username;
    private final String password;
    private final DatabaseType type;

    private DatasourceSession(String jdbcUrl, String username, String password, DatabaseType type) {
        this.id = UUID.randomUUID();
        this.datasourceId = UUID.randomUUID();
        this.jdbcUrl = jdbcUrl;
        this.username = username;
        this.password = password;
        this.type = type;
    }

    DatasourceSession(UUID datasourceId, String jdbcUrl, String username, String password, DatabaseType type) {
        this.id = UUID.randomUUID();
        this.datasourceId = datasourceId;
        this.jdbcUrl = jdbcUrl;
        this.username = username;
        this.password = password;
        this.type = type;
    }

    public static DatasourceSession create(String jdbcUrl, String username, String password, DatabaseType type) {
        return new DatasourceSession(jdbcUrl, username, password, type);
    }

    public static DatasourceSession create(UUID datasourceId, String jdbcUrl, String username, String password, DatabaseType type) {
        return new DatasourceSession(datasourceId, jdbcUrl, username, password, type);
    }

    // Getters
    public UUID getId() {
        return id;
    }

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

    public DatabaseType getType() {
        return type;
    }
}