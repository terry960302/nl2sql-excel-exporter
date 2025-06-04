package com.pandaterry.query_microservice.application.vo;

import com.pandaterry.query_microservice.application.dto.response.ColumnInfoResponse;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ColumnInfo {
    private final String columnName;
    private final String dataType;

    public static ColumnInfo from(ColumnInfoResponse response) {
        return ColumnInfo.builder()
                .columnName(response.getColumnName())
                .dataType(response.getDataType())
                .build();
    }
}