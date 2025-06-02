package com.pandaterry.application.service;

import com.pandaterry.application.exception.AgentException;
import com.pandaterry.domain.model.database.*;
import com.pandaterry.domain.service.SchemaExtractor;
import com.pandaterry.infrastructure.config.DataSourceManager;
import com.pandaterry.domain.enums.ErrorCode;
import jakarta.inject.Singleton;

import java.sql.Connection;
import java.util.List;

@Singleton
public class DatabaseConnectionService {

    private final SchemaExtractor schemaExtractor;
    private final DataSourceManager dataSourceManager;

    public DatabaseConnectionService(
            final SchemaExtractor schemaExtractor,
            final DataSourceManager dataSourceManager) {
        this.schemaExtractor = schemaExtractor;
        this.dataSourceManager = dataSourceManager;
    }

    public DatabaseConnection testConnection(String jdbcUrl, String username, String password, DatabaseType type) {
        DatabaseConnection connection = DatabaseConnection.create(jdbcUrl, username, password, type);
        return connection.testConnection();
    }

    public List<TableSchema> scanSchema(DatabaseConnection connection) {
        if (connection.getStatus() != ConnectionStatus.CONNECTED) {
            throw new AgentException(ErrorCode.DATABASE_NOT_CONNECTED);
        }

        try {
            Connection dbConnection = dataSourceManager.getDataSource(connection).getConnection();
            DatabaseConnection connectedConnection = connection.withConnection(dbConnection);
            return schemaExtractor.extractSchema(connectedConnection.getConnection());
        } catch (Exception e) {
            throw new AgentException(ErrorCode.DATABASE_SCHEMA_SCAN_FAILED, e);
        } finally {
            connection.close();
        }
    }
}