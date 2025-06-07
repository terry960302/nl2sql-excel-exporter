package com.pandaterry.msa_contracts.vo.schema;

import lombok.Getter;

import java.util.UUID;

@Getter
public class ColumnSchema {
    private final UUID id;
    private final String columnName;
    private final String dataType;
    private final boolean isNullable;
    private final boolean isPrimaryKey;

    private ColumnSchema(UUID id, String columnName, String dataType, boolean isNullable, boolean isPrimaryKey) {
        this.id = id;
        this.columnName = columnName;
        this.dataType = dataType;
        this.isNullable = isNullable;
        this.isPrimaryKey = isPrimaryKey;
    }

    public static ColumnSchema create(String columnName, String dataType, boolean isNullable, boolean isPrimaryKey) {
        return new ColumnSchema(UUID.randomUUID(), columnName, dataType, isNullable, isPrimaryKey);
    }
}