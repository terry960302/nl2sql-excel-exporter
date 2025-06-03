package com.pandaterry.application.service;

import com.pandaterry.application.exception.AgentException;
import com.pandaterry.domain.enums.ErrorCode;
import com.pandaterry.domain.model.database.DatasourceSession;
import com.pandaterry.domain.enums.DatabaseType;
import com.pandaterry.infrastructure.config.DatabaseConfig;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import jakarta.annotation.PreDestroy;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Singleton
public class DataSourceManager {

    @Inject
    private DatabaseConfig databaseConfig;
    private final Map<UUID, HikariDataSource> poolMap = new ConcurrentHashMap<>();


    public void register(DatasourceSession session) {
        UUID datasourceId = session.getDatasourceId();

        if (poolMap.containsKey(datasourceId)) {
            return;
        }

        HikariConfig config = buildConfig(session);
        HikariDataSource ds = new HikariDataSource(config);
        poolMap.put(datasourceId, ds);
    }

    // package-private : 테스트 용도로 외부에서 HikariDataSource 등록 허용을 위함.
    void registerMock(UUID datasourceId, HikariDataSource dataSource) {
        poolMap.put(datasourceId, dataSource);
    }


    public Connection getConnection(UUID datasourceId) throws SQLException {
        HikariDataSource ds = poolMap.get(datasourceId);
        if (ds == null) {
            throw new AgentException(ErrorCode.DATASOURCE_NOT_REGISTERED);
        }
        return ds.getConnection();
    }

    public void testConnection(UUID datasourceId) {
        HikariDataSource ds = poolMap.get(datasourceId);
        if(ds == null){
            throw new AgentException(ErrorCode.DATASOURCE_NOT_REGISTERED);
        }
        try(Connection conn = ds.getConnection()){
            if(!conn.isValid(5)){
                throw new AgentException(ErrorCode.DATABASE_NOT_CONNECTED);
            }
        }catch (SQLException e){
            throw new AgentException(ErrorCode.DATABASE_CONNECTION_FAILED, e);
        }
    }

    @PreDestroy
    public void shutdown() {
        poolMap.values().forEach(HikariDataSource::close);
    }

    private HikariConfig buildConfig(DatasourceSession session) {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(session.getJdbcUrl());
        config.setUsername(session.getUsername());
        config.setPassword(session.getPassword());
        config.setPoolName("pool-" + session.getId());

        String driverClassName = resolveDriverClassName(session.getType());
        config.setDriverClassName(driverClassName);

        config.setMaximumPoolSize(databaseConfig.getDefaultMaximumPoolSize());
        config.setMinimumIdle(databaseConfig.getDefaultMinimumIdle());
        config.setIdleTimeout(databaseConfig.getDefaultIdleTimeout());
        config.setConnectionTimeout(databaseConfig.getDefaultConnectionTimeout());
        config.setMaxLifetime(databaseConfig.getDefaultMaxLifetime());
        config.setLeakDetectionThreshold(databaseConfig.getDefaultConnectionTimeout());

        return config;
    }

    private String resolveDriverClassName(DatabaseType type) {
        return switch (type) {
            case POSTGRESQL -> "org.postgresql.Driver";
            case MYSQL -> "com.mysql.cj.jdbc.Driver";
            default -> throw new AgentException(ErrorCode.UNSUPPORTED_DB_TYPE);
        };
    }
}
