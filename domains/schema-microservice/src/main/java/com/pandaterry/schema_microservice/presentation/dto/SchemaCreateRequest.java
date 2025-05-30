package com.pandaterry.schema_microservice.presentation.dto;

import java.util.UUID;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class SchemaCreateRequest {
        private UUID datasourceId;
        private String name;

        public static SchemaCreateRequest of(UUID datasourceId, String name) {
                return SchemaCreateRequest.builder()
                                .datasourceId(datasourceId)
                                .name(name)
                                .build();
        }
}