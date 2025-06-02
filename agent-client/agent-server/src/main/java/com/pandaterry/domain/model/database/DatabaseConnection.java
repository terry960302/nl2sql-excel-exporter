package com.pandaterry.domain.model.database;

import com.pandaterry.application.exception.AgentException;
import com.pandaterry.domain.enums.ErrorCode;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.UUID;


public class DatabaseConnection {
    private final UUID id;
    private final String jdbcUrl;
    private final String username;
    private final String password;
    private final DatabaseType type;
    private final ConnectionStatus status;
    private final Connection connection;

    private DatabaseConnection(String jdbcUrl, String username, String password, DatabaseType type) {
        this.id = UUID.randomUUID();
        this.jdbcUrl = jdbcUrl;
        this.username = username;
        this.password = password;
        this.type = type;
        this.status = ConnectionStatus.PENDING;
        this.connection = null;
    }

    private DatabaseConnection(DatabaseConnection original, ConnectionStatus newStatus, Connection newConnection) {
        this.id = original.id;
        this.jdbcUrl = original.jdbcUrl;
        this.username = original.username;
        this.password = original.password;
        this.type = original.type;
        this.status = newStatus;
        this.connection = newConnection;
    }

    public static DatabaseConnection create(String jdbcUrl, String username, String password, DatabaseType type) {
        return new DatabaseConnection(jdbcUrl, username, password, type);
    }

    public DatabaseConnection testConnection() {
        try (Connection testConn = DriverManager.getConnection(jdbcUrl, username, password)) {
            if (!testConn.isValid(5)) {
                throw new AgentException(ErrorCode.DATABASE_CONNECTION_FAILED);
            }
            return new DatabaseConnection(this, ConnectionStatus.CONNECTED, null);
        } catch (SQLException e) {
            throw new AgentException(ErrorCode.DATABASE_CONNECTION_FAILED, e);
        }
    }

    public Connection getConnection() {
        if (this.status != ConnectionStatus.CONNECTED) {
            throw new AgentException(ErrorCode.DATABASE_NOT_CONNECTED);
        }
        return this.connection;
    }

    public DatabaseConnection withConnection(Connection connection) {
        return new DatabaseConnection(this, this.status, connection);
    }

    public DatabaseConnection close() {
        if (this.connection != null) {
            try {
                this.connection.close();
            } catch (SQLException e) {
                // 로깅만 하고 예외는 전파하지 않음
            }
        }
        return new DatabaseConnection(this, ConnectionStatus.FAILED, null);
    }

    // Getters
    public UUID getId() {
        return id;
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

    public ConnectionStatus getStatus() {
        return status;
    }
}