package com.pandaterry.query_microservice.application.vo;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

import com.pandaterry.query_microservice.application.dto.response.TableInfoResponse;

@Getter
@Builder
public class TableInfo {
    private final String tableName;
    private final List<ColumnInfo> columns;

    public static TableInfo from(TableInfoResponse response) {
        return TableInfo.builder()
                .tableName(response.getTableName())
                .columns(response.getColumns())
                .build();
    }
}