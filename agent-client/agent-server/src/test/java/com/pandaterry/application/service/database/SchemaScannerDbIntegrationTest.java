package com.pandaterry.application.service.database;

import com.pandaterry.domain.enums.DatabaseType;
import com.pandaterry.domain.model.database.*;
import com.pandaterry.domain.service.SchemaExtractor;
import com.pandaterry.infrastructure.config.DatabaseConfig;
import com.pandaterry.infrastructure.schema.PostgresSchemaExtractor;
import com.zaxxer.hikari.pool.HikariPool;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.*;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

@Testcontainers
@MicronautTest
class SchemaScannerDbIntegrationTest {

    @Container
    private static final PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15-alpine");

    @Inject
    private DatabaseConfig dbConfig;
    private SchemaScanner schemaScanner;
    @Inject
    private DataSourceManager dataSourceManager;

    private SchemaExtractor schemaExtractor;

    @BeforeEach
    void setup() {
        schemaExtractor = new PostgresSchemaExtractor();
        schemaScanner = new SchemaScanner(schemaExtractor, dataSourceManager);
        buildMockTables();
    }

    private void buildMockTables() {
        // 테스트용 테이블 생성
        try (Connection connection = DriverManager.getConnection(
                postgres.getJdbcUrl(),
                postgres.getUsername(),
                postgres.getPassword());
             Statement statement = connection.createStatement()) {

            // 테이블 생성
            statement.execute("""
                        CREATE TABLE IF NOT EXISTS users (
                            id SERIAL PRIMARY KEY,
                            username VARCHAR(50) NOT NULL,
                            email VARCHAR(100) UNIQUE,
                            created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
                        )
                    """);

            statement.execute("""
                        CREATE TABLE IF NOT EXISTS orders (
                            id SERIAL PRIMARY KEY,
                            user_id INTEGER REFERENCES users(id),
                            order_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                            total_amount DECIMAL(10,2) NOT NULL
                        )
                    """);
        } catch (Exception e) {
            throw new RuntimeException("테스트 데이터베이스 설정 실패", e);
        }
    }

    @Test
    @DisplayName("실제 데이터베이스 연결은 성공해야한다.")
    void testConnection_성공(){
        DatasourceSession session = DatasourceSession.create(
                postgres.getJdbcUrl(),
                postgres.getUsername(),
                postgres.getPassword(),
                DatabaseType.POSTGRESQL
        );

        dataSourceManager.register(session);
        dataSourceManager.testConnection(session.getDatasourceId());

        // 풀 유지 여부 확인
        try (Connection conn = dataSourceManager.getConnection(session.getDatasourceId())) {
            assertThat(conn).isNotNull();
        } catch (Exception e) {
            Assertions.fail("Connection should be available", e);
        }

    }

    @Test
    @DisplayName("잘못된 데이터베이스 연결 정보로 연결시 디비 풀 캐싱에 실패한다.")
    void testConnection_잘못된_연결정보() {
        // given
        DatasourceSession session = DatasourceSession.create(
                "jdbc:postgresql://localhost:5432/nonexistent",
                "wrong",
                "wrong",
                DatabaseType.POSTGRESQL
        );

        ;

        // when & then
        assertThatThrownBy(() -> dataSourceManager.register(session))
                .isInstanceOf(HikariPool.PoolInitializationException.class);
    }

    @Test
    @DisplayName("실제 PostgreSQL 데이터베이스에서 스키마를 스캔할 수 있어야 한다")
    void scanSchema_실제_데이터베이스() {
        // given
        DatasourceSession session = DatasourceSession.create(
                postgres.getJdbcUrl(),
                postgres.getUsername(),
                postgres.getPassword(),
                DatabaseType.POSTGRESQL
        );
        dataSourceManager.register(session);
        dataSourceManager.testConnection(session.getDatasourceId());

        // when
        List<TableSchema> schemas = schemaScanner.scanSchema(session.getDatasourceId());

        // then
        assertThat(schemas).hasSize(2);

        // users 테이블 검증
        TableSchema usersTable = schemas.stream()
                .filter(schema -> schema.getTableName().equals("users"))
                .findFirst()
                .orElseThrow();

        assertThat(usersTable.getColumns()).hasSize(4);
        assertThat(usersTable.getColumns()).anyMatch(column ->
                column.getColumnName().equals("id") &&
                        column.isPrimaryKey() &&
                        !column.isNullable()
        );
        assertThat(usersTable.getColumns()).anyMatch(column ->
                column.getColumnName().equals("username") &&
                        !column.isNullable()
        );
        assertThat(usersTable.getColumns()).anyMatch(column ->
                column.getColumnName().equals("email") &&
                        column.isNullable()
        );

        // orders 테이블 검증
        TableSchema ordersTable = schemas.stream()
                .filter(schema -> schema.getTableName().equals("orders"))
                .findFirst()
                .orElseThrow();

        assertThat(ordersTable.getColumns()).hasSize(4);
        assertThat(ordersTable.getColumns()).anyMatch(column ->
                column.getColumnName().equals("id") &&
                        column.isPrimaryKey() &&
                        !column.isNullable()
        );
        assertThat(ordersTable.getColumns()).anyMatch(column ->
                column.getColumnName().equals("total_amount") &&
                        !column.isNullable()
        );
    }

    @Test
    @DisplayName("존재하지 않는 테이블은 스키마에 포함되지 않아야 한다")
    void scanSchema_존재하지_않는_테이블() {
        // given
        DatasourceSession session = DatasourceSession.create(
                postgres.getJdbcUrl(),
                postgres.getUsername(),
                postgres.getPassword(),
                DatabaseType.POSTGRESQL
        );

        dataSourceManager.register(session);
        dataSourceManager.testConnection(session.getDatasourceId());

        // when
        List<TableSchema> schemas = schemaScanner.scanSchema(session.getDatasourceId());

        // then
        assertThat(schemas).noneMatch(schema ->
                schema.getTableName().equals("non_existent_table")
        );
    }


}