package com.pandaterry.schema_microservice.application.service;

import com.pandaterry.msa_contracts.dto.schema.request.DatasourceCreateRequest;
import com.pandaterry.msa_contracts.dto.schema.response.DatasourceResponse;
import com.pandaterry.msa_contracts.dto.schema.request.DatasourceUpdateRequest;
import com.pandaterry.schema_microservice.domain.entity.Datasource;
import com.pandaterry.msa_contracts.enums.schema.EnableStatus;
import com.pandaterry.schema_microservice.domain.exception.ErrorCode;
import com.pandaterry.schema_microservice.domain.exception.SchemaException;
import com.pandaterry.schema_microservice.infrastructure.repository.DatasourceRepository;
import com.pandaterry.schema_microservice.infrastructure.util.EncryptionUtil;
import com.pandaterry.schema_microservice.presentation.mappers.DatasourceMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class DatasourceService {
    private final DatasourceRepository datasourceRepository;

    // UUID 반환용
    public DatasourceResponse initDatasource(UUID orgId, UUID userId, UUID agentId) {
        if (orgId == null) throw new SchemaException(ErrorCode.ORG_ID_NOT_FOUND);
        Datasource datasource = Datasource.init(orgId, userId, agentId);
        Datasource saved = datasourceRepository.save(datasource);
        return DatasourceMapper.toResponse(saved);
    }

    public DatasourceResponse activateDatasource(UUID datasourceId, UUID orgId, UUID userId, UUID agentId, DatasourceUpdateRequest request) {
        if (orgId == null) throw new SchemaException(ErrorCode.ORG_ID_NOT_FOUND);

        Datasource datasource = datasourceRepository.findById(datasourceId)
                .orElseThrow(() -> new SchemaException(ErrorCode.DATASOURCE_NOT_FOUND));

        datasource.updateName(request.getName());
        datasource.updateDbType(request.getDbType());
        datasource.updateEngineType(request.getEngineType());
        datasource.deactivate();

        Datasource saved = datasourceRepository.save(datasource);
        return DatasourceMapper.toResponse(saved);
    }

    @Transactional(readOnly = true)
    public List<DatasourceResponse> getDatasources(UUID orgId) {
        if (orgId == null) throw new SchemaException(ErrorCode.ORG_ID_NOT_FOUND);
        return datasourceRepository.findByOrgIdAndIsEnabled(orgId, EnableStatus.ENABLED)
                .stream()
                .map(DatasourceMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public DatasourceResponse getDatasource(UUID datasourceId) {
        Datasource datasource = datasourceRepository.findById(datasourceId)
                .orElseThrow(() -> new SchemaException(ErrorCode.DATASOURCE_NOT_FOUND));
        return DatasourceMapper.toResponse(datasource);
    }


    public void deactivateDatasource(UUID datasourceId) {
        Datasource datasource = datasourceRepository.findById(datasourceId)
                .orElseThrow(() -> new SchemaException(ErrorCode.DATASOURCE_NOT_FOUND));
        datasource.deactivate();
    }
}