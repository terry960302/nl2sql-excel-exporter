package com.pandaterry.schema_microservice.application.service;

import com.pandaterry.schema_microservice.presentation.dto.SchemaCreateRequest;
import com.pandaterry.schema_microservice.presentation.dto.SchemaResponse;
import com.pandaterry.schema_microservice.presentation.dto.SchemaUpdateRequest;
import com.pandaterry.schema_microservice.domain.entity.Schema;
import com.pandaterry.schema_microservice.domain.enumerated.EnableStatus;
import com.pandaterry.schema_microservice.domain.exception.ErrorCode;
import com.pandaterry.schema_microservice.domain.exception.SchemaException;
import com.pandaterry.schema_microservice.infrastructure.repository.SchemaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class SchemaService {
    private final SchemaRepository schemaRepository;

    public SchemaResponse createSchema(SchemaCreateRequest request, UUID orgId, UUID createdBy) {
        if (orgId == null)
            throw new SchemaException(ErrorCode.ORG_ID_NOT_FOUND);
        Schema schema = Schema.create(orgId, request.getDatasourceId(), createdBy, request.getName());
        Schema saved = schemaRepository.save(schema);
        return SchemaResponse.of(saved);
    }

    @Transactional(readOnly = true)
    public List<SchemaResponse> getSchemasByDatasource(UUID datasourceId) {
        return schemaRepository.findByDatasourceIdAndIsEnabled(datasourceId, EnableStatus.ENABLED)
                .stream()
                .map(SchemaResponse::of)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public SchemaResponse getSchema(UUID schemaId) {
        Schema schema = schemaRepository.findById(schemaId)
                .orElseThrow(() -> new SchemaException(ErrorCode.SCHEMA_NOT_FOUND));
        return SchemaResponse.of(schema);
    }

    public SchemaResponse updateSchema(UUID schemaId, SchemaUpdateRequest request) {
        Schema schema = schemaRepository.findById(schemaId)
                .orElseThrow(() -> new SchemaException(ErrorCode.SCHEMA_NOT_FOUND));

        schema.updateNameAndStatus(request.getName(), request.getStatus());
        Schema updated = schemaRepository.save(schema);
        return SchemaResponse.of(updated);
    }

    public void deactivateSchema(UUID schemaId) {
        Schema schema = schemaRepository.findById(schemaId)
                .orElseThrow(() -> new SchemaException(ErrorCode.SCHEMA_NOT_FOUND));
        schema.deactivate();
    }
}