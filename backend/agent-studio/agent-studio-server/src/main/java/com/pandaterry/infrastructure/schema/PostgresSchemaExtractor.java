package com.pandaterry.infrastructure.schema;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pandaterry.domain.service.SchemaExtractor;
import com.pandaterry.application.exception.AgentException;
import com.pandaterry.domain.enums.ErrorCode;
import com.pandaterry.msa_contracts.enums.schema.DatabaseEngineType;
import com.pandaterry.msa_contracts.vo.schema.ColumnSchema;
import com.pandaterry.msa_contracts.vo.schema.TableSchema;
import jakarta.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class PostgresSchemaExtractor implements SchemaExtractor {

    @Inject
    private ObjectMapper objectMapper;

    private static final Logger logger = LoggerFactory.getLogger(PostgresSchemaExtractor.class);

    @Override
    public List<TableSchema> extractSchema(Connection connection) throws AgentException {
        if (connection == null) {
            throw new AgentException(ErrorCode.DATABASE_NOT_CONNECTED);
        }

        try {
            DatabaseMetaData metaData = connection.getMetaData();
            List<TableSchema> schemas = new ArrayList<>();

            try (ResultSet tables = metaData.getTables(null, null, "%", new String[]{"TABLE"})) {
                while (tables.next()) {
                    String tableName = tables.getString("TABLE_NAME");
                    try {
                        List<ColumnSchema> columns = getColumns(metaData, tableName);
                        schemas.add(TableSchema.create(tableName, columns));
                    } catch (SQLException e) {
                        logger.warn("테이블 '{}'의 컬럼 정보 추출 중 오류 발생", tableName, e);
                        // 개별 테이블 오류는 전체 프로세스를 중단하지 않음
                    }
                }
            }

            if (schemas.isEmpty()) {
                logger.warn("추출된 테이블 스키마가 없습니다");
            }

            return schemas;
        } catch (SQLException e) {
            logger.error("PostgreSQL 스키마 추출 중 오류 발생", e);
            throw new AgentException(ErrorCode.DATABASE_SCHEMA_SCAN_FAILED, e);
        }
    }

    @Override
    public String extractRawSchema(Connection connection) throws AgentException {
        if (connection == null) {
            throw new AgentException(ErrorCode.DATABASE_NOT_CONNECTED);
        }

        try {
            // PostgreSQL 시스템 카탈로그를 이용한 컬럼 정보 조회 쿼리
            String sql = ""
                    + "SELECT c.relname AS table_name, "
                    + "       a.attname AS column_name, "
                    + "       format_type(a.atttypid, a.atttypmod) AS data_type, "
                    + "       NOT a.attnotnull AS is_nullable, "
                    + "       CASE WHEN pk.indisprimary THEN 'PRI' ELSE NULL END AS column_key "
                    + "FROM pg_catalog.pg_class c "
                    + "JOIN pg_catalog.pg_namespace n ON n.oid = c.relnamespace "
                    + "JOIN pg_catalog.pg_attribute a ON a.attrelid = c.oid "
                    + "LEFT JOIN pg_catalog.pg_index pk ON pk.indrelid = c.oid "
                    + "  AND a.attnum = ANY(pk.indkey) AND pk.indisprimary "
                    + "WHERE n.nspname = ? "
                    + "  AND c.relkind = 'r' "
                    + "  AND a.attnum > 0 "
                    + "ORDER BY c.relname, a.attnum";

            List<Map<String, Object>> rawRows = new ArrayList<>();

            // PreparedStatement 생성 및 파라미터 바인딩
            try (PreparedStatement ps = connection.prepareStatement(sql)) {
                ps.setString(1, connection.getSchema()); // datasourceId를 DB 스키마 이름으로 활용

                // 쿼리 실행
                try (ResultSet rs = ps.executeQuery()) {
                    ResultSetMetaData meta = rs.getMetaData();
                    int columnCount = meta.getColumnCount();

                    // ResultSet을 순회하며 Map<String,Object> 형태로 rawRows에 담기
                    while (rs.next()) {
                        Map<String, Object> rowMap = new LinkedHashMap<>();
                        for (int i = 1; i <= columnCount; i++) {
                            String colName = meta.getColumnLabel(i);
                            Object value = rs.getObject(i);
                            rowMap.put(colName, value);
                        }
                        rawRows.add(rowMap);
                    }
                }
            }

            return objectMapper.writeValueAsString(rawRows);

        } catch (SQLException | IOException e) {
            throw new AgentException(ErrorCode.DATABASE_SCHEMA_SCAN_FAILED, e);
        }
    }

    @Override
    public boolean supports(DatabaseEngineType engineType) {
        return DatabaseEngineType.POSTGRESQL.equals(engineType);
    }

    private List<ColumnSchema> getColumns(DatabaseMetaData metaData, String tableName) throws SQLException {
        List<ColumnSchema> columns = new ArrayList<>();

        try (ResultSet rs = metaData.getColumns(null, null, tableName, null)) {
            while (rs.next()) {
                String columnName = rs.getString("COLUMN_NAME");
                String dataType = rs.getString("TYPE_NAME");
                boolean isNullable = rs.getInt("NULLABLE") == DatabaseMetaData.columnNullable;
                boolean isPrimaryKey = isPrimaryKey(metaData, tableName, columnName);

                columns.add(ColumnSchema.create(columnName, dataType, isNullable, isPrimaryKey));
            }
        }

        if (columns.isEmpty()) {
            logger.warn("테이블 '{}'에 컬럼이 없습니다", tableName);
        }

        return columns;
    }

    private boolean isPrimaryKey(DatabaseMetaData metaData, String tableName, String columnName) throws SQLException {
        try (ResultSet primaryKeys = metaData.getPrimaryKeys(null, null, tableName)) {
            while (primaryKeys.next()) {
                if (columnName.equals(primaryKeys.getString("COLUMN_NAME"))) {
                    return true;
                }
            }
        }
        return false;
    }
}