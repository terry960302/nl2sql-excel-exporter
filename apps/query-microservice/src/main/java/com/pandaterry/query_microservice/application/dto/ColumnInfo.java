package com.pandaterry.query_microservice.application.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ColumnInfo {
    private final String columnName;
    private final String dataType;
}