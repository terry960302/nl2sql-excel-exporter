package com.pandaterry.query_microservice.infrastructure.client;

import com.pandaterry.query_microservice.application.dto.DataSourceInfo;
import com.pandaterry.query_microservice.domain.enums.ErrorCode;
import com.pandaterry.query_microservice.domain.exception.QueryException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class DefaultDataSourceClient implements DataSourceClient {

    private final WebClient webClient;

    @Value("${schema.service.url}")
    private String schemaServiceUrl;

    @Override
    public Mono<DataSourceInfo> getDataSourceInfo(UUID datasourceId) {
        return webClient.get()
                .uri(schemaServiceUrl + "/api/v1/datasources/" + datasourceId)
                .retrieve()
                .bodyToMono(DataSourceInfo.class)
                .onErrorMap(e -> new QueryException(ErrorCode.INTERNAL_SERVER_ERROR));
    }
}