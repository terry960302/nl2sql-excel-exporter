package com.pandaterry.query_microservice.application.dto.response;

import java.util.List;

import com.pandaterry.query_microservice.application.vo.ColumnInfo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AccessLevel;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class TableInfoResponse {
    private String tableName;
    private List<ColumnInfo> columns;
}