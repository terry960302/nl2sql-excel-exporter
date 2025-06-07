package com.pandaterry.msa_contracts.dto.schema.request;

import com.pandaterry.msa_contracts.enums.schema.EnableStatus;

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