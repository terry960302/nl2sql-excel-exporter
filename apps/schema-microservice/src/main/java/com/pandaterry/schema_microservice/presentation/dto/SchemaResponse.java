package com.pandaterry.schema_microservice.presentation.dto;

import com.pandaterry.schema_microservice.domain.entity.Schema;
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
public class SchemaResponse {
        private UUID id;
        private UUID datasourceId;
        private String name;
        private EnableStatus isEnabled;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;

        public static SchemaResponse of(Schema schema) {
                return SchemaResponse.builder()
                                .id(schema.getId())
                                .datasourceId(schema.getDatasourceId())
                                .name(schema.getName())
                                .isEnabled(schema.getIsEnabled())
                                .createdAt(schema.getCreatedAt())
                                .updatedAt(schema.getUpdatedAt())
                                .build();
        }
}