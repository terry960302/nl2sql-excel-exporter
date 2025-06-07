package com.pandaterry.msa_contracts.dto.schema.response;

import com.pandaterry.msa_contracts.enums.schema.EnableStatus;
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
}