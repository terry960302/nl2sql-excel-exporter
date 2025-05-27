package com.pandaterry.schema_microservice.application.service;

import com.pandaterry.schema_microservice.presentation.dto.ColumnDefinitionResponse;
import com.pandaterry.schema_microservice.domain.entity.ColumnDefinition;
import com.pandaterry.schema_microservice.domain.enumerated.EnableStatus;
import com.pandaterry.schema_microservice.domain.exception.ErrorCode;
import com.pandaterry.schema_microservice.domain.exception.SchemaException;
import com.pandaterry.schema_microservice.infrastructure.repository.ColumnDefinitionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class ColumnService {
    private final ColumnDefinitionRepository columnDefinitionRepository;

    @Transactional(readOnly = true)
    public List<ColumnDefinitionResponse> getColumnsByTable(UUID tableId) {
        return columnDefinitionRepository.findByTableIdAndIsEnabled(tableId, EnableStatus.ENABLED)
                .stream()
                .map(ColumnDefinitionResponse::of)
                .toList();
    }

    @Transactional(readOnly = true)
    public ColumnDefinitionResponse getColumn(UUID columnId) {
        return columnDefinitionRepository.findById(columnId)
                .map(ColumnDefinitionResponse::of)
                .orElseThrow(() -> new SchemaException(ErrorCode.COLUMN_NOT_FOUND));
    }

    public void deactivateColumn(UUID columnId) {
        ColumnDefinition column = columnDefinitionRepository.findById(columnId)
                .orElseThrow(() -> new SchemaException(ErrorCode.COLUMN_NOT_FOUND));
        column.deactivate();
    }
}