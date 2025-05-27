package com.pandaterry.schema_microservice.integration.column;

import com.pandaterry.schema_microservice.domain.entity.ColumnDefinition;

import java.util.UUID;

public class ColumnTestDataFactory {

    public static ColumnDefinition createTestColumn(UUID tableId) {
        return ColumnDefinition.create(
                tableId,
                "test_column",
                "VARCHAR",
                true,
                false);
    }

    public static ColumnDefinition createTestColumnWithCustomName(UUID tableId, String columnName) {
        return ColumnDefinition.create(
                tableId,
                columnName,
                "VARCHAR",
                true,
                false);
    }
}