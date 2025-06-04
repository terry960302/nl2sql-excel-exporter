package com.pandaterry.application.dto.request;

import com.pandaterry.domain.model.database.TableSchema;

import java.util.List;
import java.util.UUID;

public record RegisterSchemaRequest(UUID orgId, UUID datasourceId, List<TableSchema> schemas, String rawSchema){
}
