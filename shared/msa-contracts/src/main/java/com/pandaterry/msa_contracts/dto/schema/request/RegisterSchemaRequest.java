package com.pandaterry.msa_contracts.dto.schema.request;

import com.pandaterry.msa_contracts.vo.schema.TableSchema;

import java.util.List;
import java.util.UUID;

public record RegisterSchemaRequest(
        UUID orgId,
        UUID userId,
        UUID agentId,
        UUID datasourceId,
        String name,
        List<TableSchema> schemas,
        String rawSchema) {
}
