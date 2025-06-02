package com.pandaterry.infrastructure.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import io.micronaut.context.annotation.Factory;
import io.micronaut.context.annotation.Value;
import jakarta.inject.Singleton;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


@Factory
public class DatabaseConfig {

    private final Map<String, HikariDataSource> dataSourceCache = new ConcurrentHashMap<>();

    @Value("${datasources.default.maximum-pool-size:10}")
    private int defaultMaximumPoolSize;

    @Value("${datasources.default.minimum-idle:5}")
    private int defaultMinimumIdle;

    @Value("${datasources.default.idle-timeout:30000}")
    private long defaultIdleTimeout;

    @Value("${datasources.default.connection-timeout:30000}")
    private long defaultConnectionTimeout;

    @Value("${datasources.default.max-lifetime:1800000}")
    private long defaultMaxLifetime;

    @Singleton
    public DataSourceManager dataSourceManager() {
        return new DataSourceManager(this);
    }

    public HikariDataSource createDataSource(String jdbcUrl, String username, String password, String driverClassName) {
        String key = generateKey(jdbcUrl, username);
        return dataSourceCache.computeIfAbsent(key, k -> {
            HikariConfig config = new HikariConfig();
            config.setJdbcUrl(jdbcUrl);
            config.setUsername(username);
            config.setPassword(password);
            config.setDriverClassName(driverClassName);

            // 커넥션 풀 설정
            config.setMaximumPoolSize(defaultMaximumPoolSize);
            config.setMinimumIdle(defaultMinimumIdle);
            config.setIdleTimeout(defaultIdleTimeout);
            config.setConnectionTimeout(defaultConnectionTimeout);
            config.setMaxLifetime(defaultMaxLifetime);

            return new HikariDataSource(config);
        });
    }

    private String generateKey(String jdbcUrl, String username) {
        return jdbcUrl + ":" + username;
    }

    public void closeDataSource(String jdbcUrl, String username) {
        String key = generateKey(jdbcUrl, username);
        HikariDataSource dataSource = dataSourceCache.remove(key);
        if (dataSource != null) {
            dataSource.close();
        }
    }
}