package com.pandaterry.infrastructure.schema;

import com.pandaterry.domain.enums.DatabaseType;
import com.pandaterry.domain.model.database.TableSchema;
import com.pandaterry.domain.model.database.ColumnSchema;
import com.pandaterry.domain.service.SchemaExtractor;
import com.pandaterry.application.exception.AgentException;
import com.pandaterry.domain.enums.ErrorCode;
import io.micronaut.serde.ObjectMapper;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Singleton
public class OracleSchemaExtractor implements SchemaExtractor {

    @Inject
    private ObjectMapper objectMapper;
    private static final Logger logger = LoggerFactory.getLogger(OracleSchemaExtractor.class);

    @Override
    public List<TableSchema> extractSchema(Connection connection) throws AgentException {
        if (connection == null) {
            throw new AgentException(ErrorCode.DATABASE_NOT_CONNECTED);
        }

        try {
            DatabaseMetaData metaData = connection.getMetaData();
            String schema = connection.getSchema();
            List<TableSchema> schemas = new ArrayList<>();

            try (ResultSet tables = metaData.getTables(null, schema, "%", new String[] { "TABLE" })) {
                while (tables.next()) {
                    String tableName = tables.getString("TABLE_NAME");
                    try {
                        List<ColumnSchema> columns = getColumns(metaData, schema, tableName);
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
            logger.error("Oracle 스키마 추출 중 오류 발생", e);
            throw new AgentException(ErrorCode.DATABASE_SCHEMA_SCAN_FAILED, e);
        }
    }

    @Override
    public String extractRawSchema(Connection connection) throws AgentException {
        if (connection == null) {
            throw new AgentException(ErrorCode.DATABASE_NOT_CONNECTED);
        }

        try {
            // Oracle ALL_TAB_COLUMNS 등 시스템 뷰를 이용한 컬럼 정보 조회 쿼리
            String sql = ""
                    + "SELECT c.TABLE_NAME, "
                    + "       c.COLUMN_NAME, "
                    + "       c.DATA_TYPE, "
                    + "       c.NULLABLE AS IS_NULLABLE, "
                    + "       CASE WHEN cons.CONSTRAINT_TYPE = 'P' THEN 'PRI' ELSE NULL END AS COLUMN_KEY "
                    + "FROM ALL_TAB_COLUMNS c "
                    + "LEFT JOIN ALL_CONS_COLUMNS acc ON c.OWNER = acc.OWNER "
                    + "  AND c.TABLE_NAME = acc.TABLE_NAME "
                    + "  AND c.COLUMN_NAME = acc.COLUMN_NAME "
                    + "LEFT JOIN ALL_CONSTRAINTS cons ON acc.OWNER = cons.OWNER "
                    + "  AND acc.CONSTRAINT_NAME = cons.CONSTRAINT_NAME "
                    + "  AND cons.CONSTRAINT_TYPE = 'P' "
                    + "WHERE c.OWNER = ? "
                    + "ORDER BY c.TABLE_NAME, c.COLUMN_ID";

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
    public boolean supports(DatabaseType databaseType) {
        return DatabaseType.ORACLE.equals(databaseType);
    }

    private List<ColumnSchema> getColumns(DatabaseMetaData metaData, String schema, String tableName)
            throws SQLException {
        List<ColumnSchema> columns = new ArrayList<>();

        try (ResultSet rs = metaData.getColumns(null, schema, tableName, null)) {
            while (rs.next()) {
                String columnName = rs.getString("COLUMN_NAME");
                String dataType = rs.getString("TYPE_NAME");
                boolean isNullable = rs.getInt("NULLABLE") == DatabaseMetaData.columnNullable;
                boolean isPrimaryKey = isPrimaryKey(metaData, schema, tableName, columnName);

                columns.add(ColumnSchema.create(columnName, dataType, isNullable, isPrimaryKey));
            }
        }

        if (columns.isEmpty()) {
            logger.warn("테이블 '{}'에 컬럼이 없습니다", tableName);
        }

        return columns;
    }

    private boolean isPrimaryKey(DatabaseMetaData metaData, String schema, String tableName, String columnName)
            throws SQLException {
        try (ResultSet primaryKeys = metaData.getPrimaryKeys(null, schema, tableName)) {
            while (primaryKeys.next()) {
                if (columnName.equals(primaryKeys.getString("COLUMN_NAME"))) {
                    return true;
                }
            }
        }
        return false;
    }
}