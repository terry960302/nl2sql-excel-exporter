package com.pandaterry.schema_microservice.presentation.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

import com.pandaterry.schema_microservice.domain.entity.Alias;
import com.pandaterry.schema_microservice.domain.enumerated.EnableStatus;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AliasResponse {
    private UUID id;
    private UUID columnId;
    private String aliasName;
    private String description;
    private EnableStatus isEnabled;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static AliasResponse of(Alias alias) {
        return AliasResponse.builder()
                .id(alias.getId())
                .columnId(alias.getColumnId())
                .aliasName(alias.getAliasName())
                .description(alias.getDescription())
                .isEnabled(alias.getIsEnabled())
                .createdAt(alias.getCreatedAt())
                .updatedAt(alias.getUpdatedAt())
                .build();
    }
}