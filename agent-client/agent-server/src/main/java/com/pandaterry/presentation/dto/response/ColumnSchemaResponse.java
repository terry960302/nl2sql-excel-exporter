package com.pandaterry.presentation.dto.response;

import java.util.UUID;

public record ColumnSchemaResponse(
        UUID id,
        String columnName,
        String dataType,
        boolean isNullable,
        boolean isPrimaryKey) {
}