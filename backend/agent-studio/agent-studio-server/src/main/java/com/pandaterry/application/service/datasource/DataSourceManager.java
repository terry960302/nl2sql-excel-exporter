package com.pandaterry.application.service.datasource;

import com.pandaterry.application.exception.AgentException;
import com.pandaterry.domain.enums.ErrorCode;
import com.pandaterry.domain.model.datasource.DatasourceSession;
import com.pandaterry.infrastructure.config.DatabaseConfig;
import com.pandaterry.msa_contracts.enums.schema.DatabaseEngineType;
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
        config.setUsername(session.getUsername());
        config.setPassword(session.getPassword());
        config.setConnectionTestQuery("SELECT 1");
        config.setInitializationFailTimeout(0); // 초기화 실패시 애플리케이션 크래시 방지

        HikariDataSource ds = new HikariDataSource(config);
        poolMap.put(datasourceId, ds);
    }

    // package-private : 테스트 용도로 외부에서 HikariDataSource 등록 허용을 위함.
    void registerMock(UUID datasourceId, HikariDataSource dataSource) {
        poolMap.put(datasourceId, dataSource);
    }

    public void unregister(UUID datasourceId) {
        if (!poolMap.containsKey(datasourceId)) return;

        // 우선 커넥션풀을 해제하고 캐시에서 삭제
        HikariDataSource dataSource = poolMap.get(datasourceId);
        if (!dataSource.isClosed()) {
            dataSource.close();
        }

        if (!dataSource.isClosed()) throw new AgentException(ErrorCode.DATASOURCE_NOT_CLOSED_BEFORE_REMOVE);
        poolMap.remove(datasourceId);
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
        if (ds == null) {
            throw new AgentException(ErrorCode.DATASOURCE_NOT_REGISTERED);
        }
        try (Connection conn = ds.getConnection()) {
            if (!conn.isValid(5)) {
                throw new AgentException(ErrorCode.DATABASE_NOT_CONNECTED);
            }
        } catch (SQLException e) {
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
        config.setPoolName("pool-" + session.getDatasourceId());

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

    private String resolveDriverClassName(DatabaseEngineType type) {
        return switch (type) {
            case POSTGRESQL -> "org.postgresql.Driver";
            case MYSQL -> "com.mysql.cj.jdbc.Driver";
            case ORACLE -> "oracle.jdbc.OracleDriver";
            default -> throw new AgentException(ErrorCode.INVALID_DATABASE_TYPE);
        };
    }
}
