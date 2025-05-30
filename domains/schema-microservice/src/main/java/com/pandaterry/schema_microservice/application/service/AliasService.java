package com.pandaterry.schema_microservice.application.service;

import com.pandaterry.schema_microservice.presentation.dto.AliasCreateRequest;
import com.pandaterry.schema_microservice.presentation.dto.AliasResponse;
import com.pandaterry.schema_microservice.presentation.dto.AliasUpdateRequest;
import com.pandaterry.schema_microservice.domain.entity.Alias;
import com.pandaterry.schema_microservice.domain.enumerated.EnableStatus;
import com.pandaterry.schema_microservice.domain.exception.ErrorCode;
import com.pandaterry.schema_microservice.domain.exception.SchemaException;
import com.pandaterry.schema_microservice.infrastructure.repository.AliasRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class AliasService {
    private final AliasRepository aliasRepository;

    @Transactional(readOnly = true)
    public List<AliasResponse> getAliasesByColumn(UUID columnId) {
        return aliasRepository.findByColumnIdAndIsEnabled(columnId, EnableStatus.ENABLED)
                .stream()
                .map(AliasResponse::of)
                .toList();
    }

    public AliasResponse getAlias(UUID aliasId) {
        return aliasRepository.findById(aliasId)
                .map(AliasResponse::of)
                .orElseThrow(() -> new SchemaException(ErrorCode.ALIAS_NOT_FOUND));
    }

    public AliasResponse createAlias(AliasCreateRequest request) {
        Alias alias = request.toEntity();
        return AliasResponse.of(aliasRepository.save(alias));
    }

    public AliasResponse updateAlias(UUID aliasId, AliasUpdateRequest request) {
        Alias alias = aliasRepository.findById(aliasId)
                .orElseThrow(() -> new SchemaException(ErrorCode.ALIAS_NOT_FOUND));

        alias.updateNameAndDescription(request.getAliasName(), request.getDescription());

        return AliasResponse.of(alias);
    }

    public void deactivateAlias(UUID aliasId) {
        Alias alias = aliasRepository.findById(aliasId)
                .orElseThrow(() -> new SchemaException(ErrorCode.ALIAS_NOT_FOUND));
        alias.deactivate(); // 전체 트랜젝션 걸려있으니 더티 체킹함
    }
}