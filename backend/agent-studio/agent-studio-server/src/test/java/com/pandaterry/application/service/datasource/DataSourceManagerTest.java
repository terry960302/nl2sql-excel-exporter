package com.pandaterry.application.service.datasource;

import com.pandaterry.application.exception.AgentException;
import com.pandaterry.domain.enums.ErrorCode;
import com.pandaterry.domain.model.datasource.DatasourceSession;
import com.pandaterry.infrastructure.config.DatabaseConfig;
import com.pandaterry.msa_contracts.enums.schema.DatabaseEngineType;
import com.pandaterry.presentation.dto.JdbcUrlBuilder;
import com.zaxxer.hikari.HikariDataSource;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@MicronautTest
class DataSourceManagerTest {

    @Inject
    private DatabaseConfig config;

    @Inject
    private DataSourceManager manager;

    private DatasourceSession session;

    @BeforeEach
    void setup() {
        config = mock(DatabaseConfig.class);
        when(config.getDefaultMaximumPoolSize()).thenReturn(5);
        when(config.getDefaultMinimumIdle()).thenReturn(1);
        when(config.getDefaultIdleTimeout()).thenReturn(30000L);
        when(config.getDefaultConnectionTimeout()).thenReturn(5000L);
        when(config.getDefaultMaxLifetime()).thenReturn(60000L);

        String jdbcUrl = new JdbcUrlBuilder(DatabaseEngineType.POSTGRESQL)
                .username("test")
                .password("test")
                .host("localshot")
                .port(5432)
                .database("test")
                .build();
//        UUID datasourceId = UUID.randomUUID();
        session = DatasourceSession.create(
                jdbcUrl,
                "test",
                "test",
                DatabaseEngineType.POSTGRESQL
        );
    }

    @Test
    @DisplayName("register() 호출 시 커넥션 풀이 정상 등록되어야 한다")
    void register_success() {
        // mock datasource
        HikariDataSource mockDs = mock(HikariDataSource.class);
        manager.registerMock(session.getDatasourceId(), mockDs);

        // 다시 register하면 아무 일도 일어나지 않아야 함 (중복 방지 테스트)
        manager.registerMock(session.getDatasourceId(), mockDs);
    }

    @Test
    @DisplayName("getConnection() 호출 시 등록되지 않은 데이터소스는 예외 발생")
    void getConnection_fail() {

        UUID randomId = UUID.randomUUID();
        assertThatThrownBy(() -> manager.getConnection(randomId))
                .isInstanceOf(AgentException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.DATASOURCE_NOT_REGISTERED);
    }

    @Test
    @DisplayName("testConnection() 성공 시 예외 발생하지 않음")
    void testConnection_success() throws SQLException {
        // mock 커넥션
        Connection mockConn = mock(Connection.class);
        when(mockConn.isValid(5)).thenReturn(true);

        // mock datasource
        HikariDataSource mockDs = mock(HikariDataSource.class);
        when(mockDs.getConnection()).thenReturn(mockConn);

        // 등록
        UUID dsId = UUID.randomUUID();
        manager.registerMock(dsId, mockDs);

        // 실행
        manager.testConnection(dsId);

        // 커넥션 풀 유지 여부 검증
        Connection afterConn = manager.getConnection(dsId);
        afterConn.close();

        // 검증
        verify(mockConn).isValid(5);
        verify(mockConn, times(2)).close();
        verify(mockDs, never()).close();
    }

    @Test
    @DisplayName("testConnection() 실패 시 예외 발생")
    void testConnection_fail_invalid() throws SQLException {
        // mock 커넥션
        Connection mockConn = mock(Connection.class);
        when(mockConn.isValid(5)).thenReturn(false);

        // mock datasource
        HikariDataSource mockDs = mock(HikariDataSource.class);
        when(mockDs.getConnection()).thenReturn(mockConn);

        // 등록
        UUID dsId = UUID.randomUUID();
        manager.registerMock(dsId, mockDs);

        assertThatThrownBy(() -> manager.testConnection(dsId))
                .isInstanceOf(AgentException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.DATABASE_NOT_CONNECTED);
    }

    @Test
    @DisplayName("shutdown() 시 모든 풀을 종료해야 한다")
    void shutdown_all() {
        HikariDataSource mockDs = mock(HikariDataSource.class);
        manager.registerMock(session.getDatasourceId(), mockDs);
        manager.shutdown(); // 예외가 없어야 함
    }
}
