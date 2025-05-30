package com.pandaterry.schema_microservice.unit.application.service;

import com.pandaterry.schema_microservice.application.service.TableService;
import com.pandaterry.schema_microservice.domain.entity.TableDefinition;
import com.pandaterry.schema_microservice.domain.enumerated.EnableStatus;
import com.pandaterry.schema_microservice.domain.exception.ErrorCode;
import com.pandaterry.schema_microservice.domain.exception.SchemaException;
import com.pandaterry.schema_microservice.infrastructure.repository.TableDefinitionRepository;
import com.pandaterry.schema_microservice.presentation.dto.TableDefinitionResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TableServiceTest {

    @Mock
    private TableDefinitionRepository tableDefinitionRepository;

    @InjectMocks
    private TableService tableService;

    private UUID schemaId;
    private UUID tableId;
    private TableDefinition table;

    @BeforeEach
    void setUp() {
        schemaId = UUID.randomUUID();
        tableId = UUID.randomUUID();
        table = TableDefinition.create(schemaId, "test_table");
    }

    @Test
    void getTablesBySchema_성공() {
        // given
        List<TableDefinition> tables = List.of(table);
        when(tableDefinitionRepository.findBySchemaIdAndIsEnabled(schemaId, EnableStatus.ENABLED)).thenReturn(tables);

        // when
        List<TableDefinitionResponse> result = tableService.getTablesBySchema(schemaId);

        // then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getTableName()).isEqualTo("test_table");
    }

    @Test
    void getTable_성공() {
        // given
        when(tableDefinitionRepository.findById(tableId)).thenReturn(Optional.of(table));

        // when
        TableDefinitionResponse result = tableService.getTable(tableId);

        // then
        assertThat(result.getTableName()).isEqualTo("test_table");
    }

    @Test
    void getTable_실패_테이블없음() {
        // given
        when(tableDefinitionRepository.findById(tableId)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> tableService.getTable(tableId))
                .isInstanceOf(SchemaException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.TABLE_NOT_FOUND);
    }

    @Test
    void deactivateTable_성공() {
        // given
        when(tableDefinitionRepository.findById(tableId)).thenReturn(Optional.of(table));

        // when
        tableService.deactivateTable(tableId);

        // then
        assertThat(table.getIsEnabled()).isEqualTo(EnableStatus.DISABLED);
    }

    @Test
    void deactivateTable_실패_테이블없음() {
        // given
        when(tableDefinitionRepository.findById(tableId)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> tableService.deactivateTable(tableId))
                .isInstanceOf(SchemaException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.TABLE_NOT_FOUND);
    }
}