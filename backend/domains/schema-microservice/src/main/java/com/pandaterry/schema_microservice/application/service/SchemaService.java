package com.pandaterry.schema_microservice.application.service;

import com.pandaterry.msa_contracts.dto.schema.request.RegisterSchemaRequest;
import com.pandaterry.msa_contracts.dto.schema.response.RegisterSchemaResponse;
import com.pandaterry.msa_contracts.vo.schema.ColumnSchema;
import com.pandaterry.msa_contracts.vo.schema.TableSchema;
import com.pandaterry.schema_microservice.domain.entity.ColumnDefinition;
import com.pandaterry.schema_microservice.domain.entity.Schema;
import com.pandaterry.schema_microservice.domain.entity.TableDefinition;
import com.pandaterry.schema_microservice.domain.exception.ErrorCode;
import com.pandaterry.schema_microservice.domain.exception.SchemaException;
import com.pandaterry.schema_microservice.infrastructure.repository.ColumnDefinitionRepository;
import com.pandaterry.schema_microservice.infrastructure.repository.SchemaRepository;
import com.pandaterry.schema_microservice.infrastructure.repository.TableDefinitionRepository;
import com.pandaterry.schema_microservice.presentation.mappers.SchemaMapper;
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
    private final ColumnDefinitionRepository columnDefinitionRepository;
    private final TableDefinitionRepository tableDefinitionRepository;

    public RegisterSchemaResponse uploadSchema(UUID orgId, UUID userId, UUID agentId, RegisterSchemaRequest req) {
        if (orgId == null){
            throw new SchemaException(ErrorCode.ORG_ID_NOT_FOUND);
        }

        Schema schema = Schema.create(orgId, req.datasourceId(), userId, req.name(), req.rawSchema());
        Schema saved = schemaRepository.save(schema);
        this.createTables(saved.getId(), req.schemas());
        return SchemaMapper.toResponse(saved);
    }

    private List<TableDefinition> createTables(UUID schemaId, List<TableSchema> tableDtos){
        return tableDtos.stream()
                .map(dto -> {
                    TableDefinition tableDefinition = SchemaMapper.toEntity(schemaId, dto);
                    TableDefinition saved = tableDefinitionRepository.save(tableDefinition);
                    this.createColumns(saved.getId(), dto.getColumns());
                    return saved;
                })
                .toList();

    }

    private List<ColumnDefinition> createColumns(UUID tableId, List<ColumnSchema> columns){
        return columns.stream()
                .map(t -> SchemaMapper.toEntity(tableId, t))
                .map(columnDefinitionRepository::save)
                .collect(Collectors.toList());
    }

}