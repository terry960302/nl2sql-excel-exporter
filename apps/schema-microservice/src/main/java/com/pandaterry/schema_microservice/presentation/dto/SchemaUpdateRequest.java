package com.pandaterry.schema_microservice.presentation.dto;

import com.pandaterry.schema_microservice.domain.enumerated.EnableStatus;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class SchemaUpdateRequest {
        private String name;
        private EnableStatus status;

        public static SchemaUpdateRequest of(String name, EnableStatus status) {
                return SchemaUpdateRequest.builder()
                                .name(name)
                                .status(status)
                                .build();
        }
}