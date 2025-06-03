package com.pandaterry.infrastructure.config;

import com.pandaterry.application.service.DataSourceManager;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import io.micronaut.context.annotation.Factory;
import io.micronaut.context.annotation.Value;
import jakarta.inject.Singleton;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


@Singleton
public class DatabaseConfig {

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


    public int getDefaultMaximumPoolSize() {
        return defaultMaximumPoolSize;
    }

    public int getDefaultMinimumIdle() {
        return defaultMinimumIdle;
    }

    public long getDefaultConnectionTimeout() {
        return defaultConnectionTimeout;
    }

    public long getDefaultIdleTimeout() {
        return defaultIdleTimeout;
    }

    public long getDefaultMaxLifetime() {
        return defaultMaxLifetime;
    }
}