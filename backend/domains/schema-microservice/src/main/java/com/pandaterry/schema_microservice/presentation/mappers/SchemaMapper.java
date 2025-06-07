package com.pandaterry.schema_microservice.presentation.mappers;

import com.pandaterry.msa_contracts.dto.schema.response.RegisterSchemaResponse;
import com.pandaterry.msa_contracts.vo.schema.ColumnSchema;
import com.pandaterry.msa_contracts.vo.schema.TableSchema;
import com.pandaterry.schema_microservice.domain.entity.ColumnDefinition;
import com.pandaterry.schema_microservice.domain.entity.Schema;
import com.pandaterry.schema_microservice.domain.entity.TableDefinition;

import java.util.UUID;

public class SchemaMapper {
    public static RegisterSchemaResponse toResponse(Schema schema) {
        return new RegisterSchemaResponse(schema.getId(), schema.getName(), schema.getRawJson());
    }

    public static TableDefinition toEntity(UUID schemaId, TableSchema table) {
        return TableDefinition.create(schemaId, table.getTableName());
    }

    public static ColumnDefinition toEntity(UUID tableId, ColumnSchema column) {
        return ColumnDefinition.create(tableId, column.getColumnName(), column.getDataType(), column.isNullable(), column.isPrimaryKey());
    }
}
