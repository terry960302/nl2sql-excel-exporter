package com.pandaterry.schema_microservice.application.service;

import com.pandaterry.schema_microservice.presentation.dto.TableDefinitionResponse;
import com.pandaterry.schema_microservice.domain.entity.TableDefinition;
import com.pandaterry.schema_microservice.domain.enumerated.EnableStatus;
import com.pandaterry.schema_microservice.domain.exception.ErrorCode;
import com.pandaterry.schema_microservice.domain.exception.SchemaException;
import com.pandaterry.schema_microservice.infrastructure.repository.TableDefinitionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class TableService {
    private final TableDefinitionRepository tableDefinitionRepository;

    @Transactional(readOnly = true)
    public List<TableDefinitionResponse> getTablesBySchema(UUID schemaId) {
        return tableDefinitionRepository.findBySchemaIdAndIsEnabled(schemaId, EnableStatus.ENABLED)
                .stream()
                .map(TableDefinitionResponse::of)
                .toList();
    }

    @Transactional(readOnly = true)
    public TableDefinitionResponse getTable(UUID tableId) {
        return tableDefinitionRepository.findById(tableId)
                .map(TableDefinitionResponse::of)
                .orElseThrow(() -> new SchemaException(ErrorCode.TABLE_NOT_FOUND));
    }

    public void deactivateTable(UUID tableId) {
        TableDefinition table = tableDefinitionRepository.findById(tableId)
                .orElseThrow(() -> new SchemaException(ErrorCode.TABLE_NOT_FOUND));
        table.deactivate();
    }
}