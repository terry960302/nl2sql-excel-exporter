package com.pandaterry.query_microservice.application.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class TableInfo {
    private final String tableName;
    private final List<ColumnInfo> columns;
}