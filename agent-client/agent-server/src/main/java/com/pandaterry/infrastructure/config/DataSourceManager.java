package com.pandaterry.infrastructure.config;

import com.pandaterry.domain.model.database.DatabaseConnection;
import com.pandaterry.domain.model.database.DatabaseType;
import com.zaxxer.hikari.HikariDataSource;
import jakarta.inject.Singleton;
import javax.sql.DataSource;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Singleton
public class DataSourceManager {
    private final DatabaseConfig databaseConfig;
    private final Map<String, DataSource> dataSourceMap = new ConcurrentHashMap<>();

    public DataSourceManager(DatabaseConfig databaseConfig) {
        this.databaseConfig = databaseConfig;
    }

    public DataSource getDataSource(DatabaseConnection connection) {
        String key = generateKey(connection);
        return dataSourceMap.computeIfAbsent(key, k -> {
            String driverClassName = getDriverClassName(connection.getType());
            return databaseConfig.createDataSource(
                connection.getJdbcUrl(),
                connection.getUsername(),
                connection.getPassword(),
                driverClassName
            );
        });
    }

    public void removeDataSource(DatabaseConnection connection) {
        String key = generateKey(connection);
        DataSource dataSource = dataSourceMap.remove(key);
        if (dataSource instanceof HikariDataSource) {
            ((HikariDataSource) dataSource).close();
        }
    }

    private String generateKey(DatabaseConnection connection) {
        return connection.getJdbcUrl() + ":" + connection.getUsername();
    }

    private String getDriverClassName(DatabaseType type) {
        return type.getDriver();
    }
}
