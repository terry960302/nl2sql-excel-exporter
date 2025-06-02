package com.pandaterry.infrastructure.schema;

import com.pandaterry.domain.model.database.TableSchema;
import com.pandaterry.domain.model.database.ColumnSchema;
import com.pandaterry.domain.service.SchemaExtractor;
import com.pandaterry.application.exception.AgentException;
import com.pandaterry.domain.enums.ErrorCode;
import jakarta.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Singleton
public class OracleSchemaExtractor implements SchemaExtractor {
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
    public boolean supports(String databaseType) {
        return "ORACLE".equalsIgnoreCase(databaseType);
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