package com.pandaterry.application.dto.request;

import com.pandaterry.domain.model.database.TableSchema;
import java.util.List;

public record DataSourceRegistrationRequest(
                String agentId,
                String databaseType,
                String connectionString,
                List<TableSchema> schemas) {
}