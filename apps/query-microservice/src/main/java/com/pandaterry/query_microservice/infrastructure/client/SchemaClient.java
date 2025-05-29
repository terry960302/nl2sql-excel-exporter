package com.pandaterry.query_microservice.infrastructure.client;

import com.pandaterry.query_microservice.application.dto.DataSourceInfo;
import com.pandaterry.query_microservice.application.dto.response.SchemaInfoResponse;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.UUID;

public interface SchemaClient {
    // 조직의 모든 스키마 정보를 조회합니다.
    Mono<List<SchemaInfoResponse>> getSchemasForOrg(UUID orgId);

    // 데이터소스의 연결 정보를 조회합니다.
    Mono<DataSourceInfo> getDataSourceInfo(UUID datasourceId);

    // 조직의 모든 스키마 정보와 별칭을 함께 조회합니다.
    Mono<List<SchemaInfoResponse>> getSchemasWithAliases(UUID orgId);
}