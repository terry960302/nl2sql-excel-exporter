package com.pandaterry.query_microservice.application.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class SchemaInfo {
    private final String schemaName;
    private final List<TableInfo> tables;
    private final List<AliasInfo> aliases;
}