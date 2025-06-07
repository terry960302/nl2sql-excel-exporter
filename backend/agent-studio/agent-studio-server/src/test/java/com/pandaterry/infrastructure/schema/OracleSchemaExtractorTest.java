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

class OracleSchemaExtractorTest {

    private OracleSchemaExtractor extractor;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() throws Exception {
        objectMapper = ObjectMapper.getDefault();
        extractor = new OracleSchemaExtractor();
        Field f = OracleSchemaExtractor.class.getDeclaredField("objectMapper");
        f.setAccessible(true);
        f.set(extractor, objectMapper);
    }

    @Test
    @DisplayName("Oracle 시스템 뷰 쿼리로 RAW 스키마를 추출한다")
    void extractRawSchema() throws Exception {
        Connection connection = mock(Connection.class);
        PreparedStatement ps = mock(PreparedStatement.class);
        ResultSet rs = mock(ResultSet.class);
        ResultSetMetaData meta = mock(ResultSetMetaData.class);

        when(connection.getSchema()).thenReturn("TEST");
        when(connection.prepareStatement(anyString())).thenReturn(ps);
        when(ps.executeQuery()).thenReturn(rs);
        when(rs.getMetaData()).thenReturn(meta);
        when(meta.getColumnCount()).thenReturn(5);
        when(meta.getColumnLabel(1)).thenReturn("TABLE_NAME");
        when(meta.getColumnLabel(2)).thenReturn("COLUMN_NAME");
        when(meta.getColumnLabel(3)).thenReturn("DATA_TYPE");
        when(meta.getColumnLabel(4)).thenReturn("IS_NULLABLE");
        when(meta.getColumnLabel(5)).thenReturn("COLUMN_KEY");

        when(rs.next()).thenReturn(true, false);
        when(rs.getObject(1)).thenReturn("USERS");
        when(rs.getObject(2)).thenReturn("ID");
        when(rs.getObject(3)).thenReturn("NUMBER");
        when(rs.getObject(4)).thenReturn("N");
        when(rs.getObject(5)).thenReturn("PRI");

        String json = extractor.extractRawSchema(connection);

        ArgumentCaptor<String> sqlCaptor = ArgumentCaptor.forClass(String.class);
        verify(connection).prepareStatement(sqlCaptor.capture());
        assertThat(sqlCaptor.getValue()).contains("ALL_TAB_COLUMNS");

        List<Map<String, Object>> expected = new ArrayList<>();
        Map<String, Object> row = new LinkedHashMap<>();
        row.put("TABLE_NAME", "USERS");
        row.put("COLUMN_NAME", "ID");
        row.put("DATA_TYPE", "NUMBER");
        row.put("IS_NULLABLE", "N");
        row.put("COLUMN_KEY", "PRI");
        expected.add(row);

        assertThat(json).isEqualTo(objectMapper.writeValueAsString(expected));
    }
}
