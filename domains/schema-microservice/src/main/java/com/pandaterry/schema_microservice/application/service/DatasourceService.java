package com.pandaterry.schema_microservice.application.service;

import com.pandaterry.schema_microservice.presentation.dto.DatasourceCreateRequest;
import com.pandaterry.schema_microservice.presentation.dto.DatasourceResponse;
import com.pandaterry.schema_microservice.presentation.dto.DatasourceUpdateRequest;
import com.pandaterry.schema_microservice.domain.entity.Datasource;
import com.pandaterry.schema_microservice.domain.enumerated.EnableStatus;
import com.pandaterry.schema_microservice.domain.exception.ErrorCode;
import com.pandaterry.schema_microservice.domain.exception.SchemaException;
import com.pandaterry.schema_microservice.infrastructure.repository.DatasourceRepository;
import com.pandaterry.schema_microservice.infrastructure.util.EncryptionUtil;
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
    private final EncryptionUtil encryptionUtil;

    public DatasourceResponse createDatasource(UUID orgId, DatasourceCreateRequest request) {
        if(orgId == null) throw new SchemaException(ErrorCode.ORG_ID_NOT_FOUND);
        String encryptedPw = encryptionUtil.encrypt(request.getPassword());
        Datasource datasource = request.toEntity(orgId, encryptedPw);
        Datasource saved = datasourceRepository.save(datasource);
        return DatasourceResponse.of(saved);
    }

    @Transactional(readOnly = true)
    public List<DatasourceResponse> getDatasources(UUID orgId) {
        if(orgId == null) throw new SchemaException(ErrorCode.ORG_ID_NOT_FOUND);
        return datasourceRepository.findByOrgIdAndIsEnabled(orgId, EnableStatus.ENABLED)
                .stream()
                .map(DatasourceResponse::of)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public DatasourceResponse getDatasource(UUID datasourceId) {
        Datasource datasource = datasourceRepository.findById(datasourceId)
                .orElseThrow(() -> new SchemaException(ErrorCode.DATASOURCE_NOT_FOUND));
        return DatasourceResponse.of(datasource);
    }

    public DatasourceResponse updateDatasource(UUID datasourceId, DatasourceUpdateRequest request) {
        Datasource datasource = datasourceRepository.findById(datasourceId)
                .orElseThrow(() -> new SchemaException(ErrorCode.DATASOURCE_NOT_FOUND));

        datasource.updateName(request.getName());
        datasource.updateEndpoint(request.getEndpoint());
        datasource.updateUsername(request.getUsername());
        datasource.updatePassword(encryptionUtil.encrypt(request.getPassword()));
        datasource.updateSslEnabled(request.isSslEnabled());
        datasource.updateOptions(request.getOptions());

        Datasource updated = datasourceRepository.save(datasource);
        return DatasourceResponse.of(updated);
    }

    public void deactivateDatasource(UUID datasourceId) {
        Datasource datasource = datasourceRepository.findById(datasourceId)
                .orElseThrow(() -> new SchemaException(ErrorCode.DATASOURCE_NOT_FOUND));
        datasource.deactivate();
    }
}