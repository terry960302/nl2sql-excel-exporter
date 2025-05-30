package com.pandaterry.schema_microservice.unit.application.service;

import com.pandaterry.schema_microservice.application.service.ColumnService;
import com.pandaterry.schema_microservice.domain.entity.ColumnDefinition;
import com.pandaterry.schema_microservice.domain.enumerated.EnableStatus;
import com.pandaterry.schema_microservice.domain.exception.ErrorCode;
import com.pandaterry.schema_microservice.domain.exception.SchemaException;
import com.pandaterry.schema_microservice.infrastructure.repository.ColumnDefinitionRepository;
import com.pandaterry.schema_microservice.presentation.dto.ColumnDefinitionResponse;
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
class ColumnServiceTest {

    @Mock
    private ColumnDefinitionRepository columnDefinitionRepository;

    @InjectMocks
    private ColumnService columnService;

    private UUID tableId;
    private UUID columnId;
    private ColumnDefinition column;

    @BeforeEach
    void setUp() {
        tableId = UUID.randomUUID();
        columnId = UUID.randomUUID();
        column = ColumnDefinition.create(tableId, "test_column", "VARCHAR", true, false);
    }

    @Test
    void getColumnsByTable_성공() {
        // given
        List<ColumnDefinition> columns = List.of(column);
        when(columnDefinitionRepository.findByTableIdAndIsEnabled(tableId, EnableStatus.ENABLED)).thenReturn(columns);

        // when
        List<ColumnDefinitionResponse> result = columnService.getColumnsByTable(tableId);

        // then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getColumnName()).isEqualTo("test_column");
    }

    @Test
    void getColumn_성공() {
        // given
        when(columnDefinitionRepository.findById(columnId)).thenReturn(Optional.of(column));

        // when
        ColumnDefinitionResponse result = columnService.getColumn(columnId);

        // then
        assertThat(result.getColumnName()).isEqualTo("test_column");
    }

    @Test
    void getColumn_실패_컬럼없음() {
        // given
        when(columnDefinitionRepository.findById(columnId)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> columnService.getColumn(columnId))
                .isInstanceOf(SchemaException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.COLUMN_NOT_FOUND);
    }

    @Test
    void deactivateColumn_성공() {
        // given
        when(columnDefinitionRepository.findById(columnId)).thenReturn(Optional.of(column));

        // when
        columnService.deactivateColumn(columnId);

        // then
        assertThat(column.getIsEnabled()).isEqualTo(EnableStatus.DISABLED);
    }

    @Test
    void deactivateColumn_실패_컬럼없음() {
        // given
        when(columnDefinitionRepository.findById(columnId)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> columnService.deactivateColumn(columnId))
                .isInstanceOf(SchemaException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.COLUMN_NOT_FOUND);
    }
}