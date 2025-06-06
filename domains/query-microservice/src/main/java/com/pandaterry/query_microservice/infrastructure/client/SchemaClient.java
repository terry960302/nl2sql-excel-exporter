package com.pandaterry.query_microservice.infrastructure.client;

import com.pandaterry.msa_contracts.dto.query.response.SchemaInfoResponse;
import com.pandaterry.query_microservice.application.vo.DataSourceInfo;
import com.pandaterry.query_microservice.domain.enums.ErrorCode;
import com.pandaterry.query_microservice.domain.exception.QueryException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class SchemaClient {

    private final WebClient webClient;

    @Value("${schema.service.url}")
    private String schemaServiceUrl;

    public Mono<List<SchemaInfoResponse>> getSchemasForOrg(UUID orgId) {
        return webClient.get()
                .uri(schemaServiceUrl + "/api/v1/schemas") // 쿼리 파라미터 제거
                .header("X-Organization-Id", orgId.toString()) // 헤더로 추가
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<SchemaInfoResponse>>() {
                })
                .onErrorMap(e -> new QueryException(ErrorCode.INTERNAL_SERVER_ERROR));
    }

    public Mono<DataSourceInfo> getDataSourceInfo(UUID datasourceId) {
        return webClient.get()
                .uri(schemaServiceUrl + "/api/v1/datasources/" + datasourceId)
                .retrieve()
                .bodyToMono(DataSourceInfo.class)
                .onErrorMap(e -> new QueryException(ErrorCode.INTERNAL_SERVER_ERROR));
    }

    public Mono<List<SchemaInfoResponse>> getSchemasWithAliases(UUID orgId) {
        return webClient.get()
                .uri(schemaServiceUrl + "/v1/schemas/with-aliases?orgId=" + orgId)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<SchemaInfoResponse>>() {
                })
                .onErrorMap(e -> new QueryException(ErrorCode.INTERNAL_SERVER_ERROR));
    }
}