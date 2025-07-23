package com.pandaterry.msa_contracts.vo.schema;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.micronaut.serde.annotation.Serdeable;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.UUID;

@Serdeable
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class ColumnSchema implements Serializable {
    private UUID id;
    private String columnName;
    private String dataType;
    @JsonProperty("nullable") // JSON의 "nullable"을 isNullable과 매핑
    private boolean isNullable;
    @JsonProperty("primaryKey")
    private boolean isPrimaryKey;

    public static ColumnSchema create(String columnName, String dataType, boolean isNullable, boolean isPrimaryKey) {
        return new ColumnSchema(UUID.randomUUID(), columnName, dataType, isNullable, isPrimaryKey);
    }
}