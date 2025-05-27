package com.pandaterry.schema_microservice.presentation.dto;

import com.pandaterry.schema_microservice.domain.entity.TableDefinition;
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
public class TableDefinitionResponse {
        private UUID id;
        private UUID schemaId;
        private String tableName;
        private EnableStatus isEnabled;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;

        public static TableDefinitionResponse of(TableDefinition table) {
                return TableDefinitionResponse.builder()
                                .id(table.getId())
                                .schemaId(table.getSchemaId())
                                .tableName(table.getTableName())
                                .isEnabled(table.getIsEnabled())
                                .createdAt(table.getCreatedAt())
                                .updatedAt(table.getUpdatedAt())
                                .build();
        }
}