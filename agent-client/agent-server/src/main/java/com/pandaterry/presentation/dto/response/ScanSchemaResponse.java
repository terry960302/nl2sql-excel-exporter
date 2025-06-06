package com.pandaterry.presentation.dto.response;

import com.pandaterry.domain.model.database.TableSchema;

import java.util.List;
import java.util.UUID;

public record ScanSchemaResponse(UUID datasourceId, List<TableSchema> schemas, String rawSchema){}
