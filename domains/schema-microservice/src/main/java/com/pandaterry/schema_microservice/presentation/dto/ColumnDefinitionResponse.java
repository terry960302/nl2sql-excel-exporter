package com.pandaterry.schema_microservice.presentation.dto;

import com.pandaterry.schema_microservice.domain.entity.ColumnDefinition;
import com.pandaterry.schema_microservice.domain.enumerated.EnableStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ColumnDefinitionResponse {
        private UUID id;
        private UUID tableId;
        private String columnName;
        private String dataType;
        private boolean isNullable;
        private boolean isPrimaryKey;
        private EnableStatus isEnabled;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;

        public static ColumnDefinitionResponse of(ColumnDefinition column) {
                return ColumnDefinitionResponse.builder()
                                .id(column.getId())
                                .tableId(column.getTableId())
                                .columnName(column.getColumnName())
                                .dataType(column.getDataType())
                                .isNullable(column.isNullable())
                                .isPrimaryKey(column.isPrimaryKey())
                                .isEnabled(column.getIsEnabled())
                                .createdAt(column.getCreatedAt())
                                .updatedAt(column.getUpdatedAt())
                                .build();
        }
}