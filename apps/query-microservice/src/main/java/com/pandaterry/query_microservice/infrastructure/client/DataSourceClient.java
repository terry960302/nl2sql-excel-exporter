package com.pandaterry.query_microservice.infrastructure.client;

import com.pandaterry.query_microservice.application.dto.DataSourceInfo;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface DataSourceClient {
    /**
     * 데이터소스의 연결 정보를 조회합니다.
     * 
     * @param datasourceId 데이터소스 ID
     * @return 데이터소스 연결 정보
     */
    Mono<DataSourceInfo> getDataSourceInfo(UUID datasourceId);
}