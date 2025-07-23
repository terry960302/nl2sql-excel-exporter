package com.pandaterry.msa_contracts.vo.schema;

import io.micronaut.serde.annotation.Serdeable;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;
import java.util.UUID;

@Serdeable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class TableSchema implements Serializable {
    private UUID id;
    private String tableName;
    private List<ColumnSchema> columns;

    public static TableSchema create(String tableName, List<ColumnSchema> columns) {
        return new TableSchema(UUID.randomUUID(), tableName, columns);
    }

}