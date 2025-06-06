package com.pandaterry.infrastructure.schema;

import io.micronaut.serde.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class PostgresSchemaExtractorTest {

    private PostgresSchemaExtractor extractor;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() throws Exception {
        objectMapper = ObjectMapper.getDefault();
        extractor = new PostgresSchemaExtractor();
        Field f = PostgresSchemaExtractor.class.getDeclaredField("objectMapper");
        f.setAccessible(true);
        f.set(extractor, objectMapper);
    }

    @Test
    @DisplayName("PostgreSQL 시스템 카탈로그 쿼리로 RAW 스키마를 추출한다")
    void extractRawSchema() throws Exception {
        Connection connection = mock(Connection.class);
        PreparedStatement ps = mock(PreparedStatement.class);
        ResultSet rs = mock(ResultSet.class);
        ResultSetMetaData meta = mock(ResultSetMetaData.class);

        when(connection.getSchema()).thenReturn("public");
        when(connection.prepareStatement(anyString())).thenReturn(ps);
        when(ps.executeQuery()).thenReturn(rs);
        when(rs.getMetaData()).thenReturn(meta);
        when(meta.getColumnCount()).thenReturn(5);
        when(meta.getColumnLabel(1)).thenReturn("table_name");
        when(meta.getColumnLabel(2)).thenReturn("column_name");
        when(meta.getColumnLabel(3)).thenReturn("data_type");
        when(meta.getColumnLabel(4)).thenReturn("is_nullable");
        when(meta.getColumnLabel(5)).thenReturn("column_key");

        when(rs.next()).thenReturn(true, false);
        when(rs.getObject(1)).thenReturn("users");
        when(rs.getObject(2)).thenReturn("id");
        when(rs.getObject(3)).thenReturn("integer");
        when(rs.getObject(4)).thenReturn(true);
        when(rs.getObject(5)).thenReturn("PRI");

        String json = extractor.extractRawSchema(connection);

        ArgumentCaptor<String> sqlCaptor = ArgumentCaptor.forClass(String.class);
        verify(connection).prepareStatement(sqlCaptor.capture());
        assertThat(sqlCaptor.getValue()).contains("pg_catalog");

        List<Map<String, Object>> expected = new ArrayList<>();
        Map<String, Object> row = new LinkedHashMap<>();
        row.put("table_name", "users");
        row.put("column_name", "id");
        row.put("data_type", "integer");
        row.put("is_nullable", true);
        row.put("column_key", "PRI");
        expected.add(row);

        assertThat(json).isEqualTo(objectMapper.writeValueAsString(expected));
    }
}
