package com.pandaterry.query_microservice.application.service;

import com.pandaterry.msa_contracts.dto.query.response.SchemaInfoResponse;
import com.pandaterry.query_microservice.infrastructure.client.SchemaClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SchemaService {
    private final SchemaClient schemaClient;

    public Mono<List<SchemaInfoResponse>> getSchemasWithAliases(UUID orgId) {
        return schemaClient.getSchemasWithAliases(orgId);
    }
}