package com.pandaterry.presentation.dto.request;

import com.pandaterry.msa_contracts.vo.schema.TableSchema;

import java.util.List;

public record DataSourceRegistrationRequest(
                String agentId,
                String databaseType,
                String connectionString,
                List<TableSchema> schemas) {
}