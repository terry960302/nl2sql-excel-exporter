package com.pandaterry.msa_contracts.vo.schema;

import java.util.List;
import java.util.UUID;

public class TableSchema {
    private final UUID id;
    private final String tableName;
    private final List<ColumnSchema> columns;

    private TableSchema(UUID id, String tableName, List<ColumnSchema> columns) {
        this.id = id;
        this.tableName = tableName;
        this.columns = columns;
    }

    public static TableSchema create(String tableName, List<ColumnSchema> columns) {
        return new TableSchema(UUID.randomUUID(), tableName, columns);
    }

    // Getters
    public UUID getId() {
        return id;
    }

    public String getTableName() {
        return tableName;
    }

    public List<ColumnSchema> getColumns() {
        return columns;
    }
}