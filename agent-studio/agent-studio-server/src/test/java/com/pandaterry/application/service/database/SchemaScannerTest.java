package com.pandaterry.application.service.database;

import com.pandaterry.domain.model.database.*;
import com.pandaterry.domain.service.SchemaExtractor;
import com.pandaterry.application.exception.AgentException;
import com.pandaterry.application.service.database.DataSourceManager;
import com.pandaterry.application.service.database.SchemaScanner;
import com.pandaterry.domain.enums.ErrorCode;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@MicronautTest
class SchemaScannerTest {

    private SchemaExtractor schemaExtractor;
    private DataSourceManager dataSourceManager;
    private Connection connection;
    private SchemaScanner scanner;

    private final UUID datasourceId = UUID.randomUUID();

    @BeforeEach
    public void setup(){
        schemaExtractor = mock(SchemaExtractor.class);
        dataSourceManager = mock(DataSourceManager.class);
        connection = mock(Connection.class);
        scanner = new SchemaScanner(schemaExtractor, dataSourceManager);
    }

    @Test
    @DisplayName("scanSchema 성공 시 테이블 스키마 목록을 반환해야 한다")
    void scanSchema_성공() throws Exception {
        // given
        List<TableSchema> expectedSchemas = List.of(
                TableSchema.create("test_table", List.of())
        );

        when(dataSourceManager.getConnection(datasourceId)).thenReturn(connection);
        when(schemaExtractor.extractSchema(connection)).thenReturn(expectedSchemas);

        // when
        List<TableSchema> result = scanner.scanSchema(datasourceId);

        // then
        assertThat(result).isEqualTo(expectedSchemas);
        verify(schemaExtractor).extractSchema(connection);
        verify(connection).close();
    }

    @Test
    @DisplayName("scanSchema 실패 시 AgentException을 발생시켜야 한다")
    void scanSchema_실패() throws Exception {
        // given
        when(dataSourceManager.getConnection(datasourceId)).thenThrow(new SQLException("Connection failed"));

        // when & then
        assertThatThrownBy(() -> scanner.scanSchema(datasourceId))
                .isInstanceOf(AgentException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.DATABASE_SCHEMA_SCAN_FAILED);
    }
}