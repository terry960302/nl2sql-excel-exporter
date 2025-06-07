package com.pandaterry.msa_contracts.dto.schema.response;

import com.pandaterry.msa_contracts.enums.schema.DatabaseEngineType;
import com.pandaterry.msa_contracts.enums.schema.DatabaseType;
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
public class DatasourceResponse {
    private UUID id;
    private String name;
    private DatabaseType dbType;
    private DatabaseEngineType engineType;
    private EnableStatus isEnabled;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}