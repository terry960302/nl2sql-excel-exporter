package com.pandaterry.domain.model.database;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@Testcontainers
class DatabaseConnectionTest {

    // 테스트를 위한 임시 Postgresql 컨테이너를 띄움.
    @Container
    private static final PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15-alpine");

    @Test
    @DisplayName("유효한 데이터베이스 연결 정보로 연결 테스트 시 성공해야 한다")
    void testConnection_성공() {
        // given
        String jdbcUrl = postgres.getJdbcUrl();
        String username = postgres.getUsername();
        String password = postgres.getPassword();
        DatabaseConnection connection = DatabaseConnection.create(jdbcUrl, username, password, DatabaseType.POSTGRESQL);

        // when
        connection.testConnection();

        // then
        assertThat(connection.getStatus()).isEqualTo(ConnectionStatus.CONNECTED);
    }

    @Test
    @DisplayName("잘못된 데이터베이스 연결 정보로 연결 테스트 시 실패해야 한다")
    void testConnection_실패() {
        // given
        String jdbcUrl = "jdbc:postgresql://localhost:5432/nonexistent";
        String username = "wrong";
        String password = "wrong";
        DatabaseConnection connection = DatabaseConnection.create(jdbcUrl, username, password, DatabaseType.POSTGRESQL);

        // when & then
        assertThatThrownBy(() -> connection.testConnection())
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Failed to connect to database");
        assertThat(connection.getStatus()).isEqualTo(ConnectionStatus.FAILED);
    }
}