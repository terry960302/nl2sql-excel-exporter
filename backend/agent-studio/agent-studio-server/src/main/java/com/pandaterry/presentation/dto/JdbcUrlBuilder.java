package com.pandaterry.presentation.dto;

import com.pandaterry.application.exception.AgentException;
import com.pandaterry.domain.enums.ErrorCode;
import com.pandaterry.msa_contracts.enums.schema.DatabaseEngineType;
import com.pandaterry.msa_contracts.enums.schema.DatabaseType;

import java.util.HashMap;
import java.util.Map;

public class JdbcUrlBuilder {
    private final DatabaseEngineType engineType;
    private String host;
    private int port;
    private String username;
    private String password;
    private String databaseName;
    private Map<String, String> options = new HashMap<>();

    private static final String MYSQL_URL_TEMPLATE = "jdbc:mysql://%s:%d/%s";
    private static final String POSTGRESQL_URL_TEMPLATE = "jdbc:postgresql://%s:%d/%s";
    private static final String ORACLE_URL_TEMPLATE = "jdbc:oracle:thin:@%s:%d:%s";

    public JdbcUrlBuilder(DatabaseEngineType engineType) {
        this.engineType = engineType;
    }

    public JdbcUrlBuilder host(String host) {
        this.host = host;
        return this;
    }

    public JdbcUrlBuilder port(int port) {
        this.port = port;
        return this;
    }

    public JdbcUrlBuilder username(String username) {
        this.username = username;
        return this;
    }

    public JdbcUrlBuilder password(String password) {
        this.password = password;
        return this;
    }

    public JdbcUrlBuilder database(String databaseName) {
        this.databaseName = databaseName;
        return this;
    }

    public JdbcUrlBuilder option(String key, String value) {
        this.options.put(key, value);
        return this;
    }

    public String build() {
        return switch (engineType) {
            case MYSQL -> String.format(MYSQL_URL_TEMPLATE, host, port, databaseName);
            case POSTGRESQL -> String.format(POSTGRESQL_URL_TEMPLATE, host, port, databaseName);
            case ORACLE -> String.format(ORACLE_URL_TEMPLATE, host, port, databaseName);
        };
    }
}
