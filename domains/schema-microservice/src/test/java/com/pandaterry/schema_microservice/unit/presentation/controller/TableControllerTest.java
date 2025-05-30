package com.pandaterry.schema_microservice.unit.presentation.controller;

import com.pandaterry.schema_microservice.application.service.TableService;
import com.pandaterry.schema_microservice.domain.enumerated.EnableStatus;
import com.pandaterry.schema_microservice.domain.exception.ErrorCode;
import com.pandaterry.schema_microservice.domain.exception.SchemaException;
import com.pandaterry.schema_microservice.presentation.controller.TableController;
import com.pandaterry.schema_microservice.presentation.dto.TableDefinitionResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TableController.class)
class TableControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private TableService tableService;

    private UUID schemaId;
    private UUID tableId;
    private TableDefinitionResponse testTableResponse;

    @BeforeEach
    void setUp() {
        schemaId = UUID.randomUUID();
        tableId = UUID.randomUUID();

        testTableResponse = TableDefinitionResponse.builder()
                .id(tableId)
                .schemaId(schemaId)
                .tableName("test_table")
                .isEnabled(EnableStatus.ENABLED)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    @Test
    @DisplayName("테이블 목록 조회 성공")
    void getTablesBySchema_성공() throws Exception {
        // given
        List<TableDefinitionResponse> tables = Arrays.asList(testTableResponse);
        when(tableService.getTablesBySchema(schemaId)).thenReturn(tables);

        // when & then
        mockMvc.perform(get("/schemas/{schemaId}/tables", schemaId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(tableId.toString()))
                .andExpect(jsonPath("$[0].tableName").value("test_table"));

        verify(tableService).getTablesBySchema(schemaId);
    }

    @Test
    @DisplayName("단일 테이블 조회 성공")
    void getTable_성공() throws Exception {
        // given
        when(tableService.getTable(tableId)).thenReturn(testTableResponse);

        // when & then
        mockMvc.perform(get("/schemas/{schemaId}/tables/{tableId}", schemaId, tableId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(tableId.toString()))
                .andExpect(jsonPath("$.tableName").value("test_table"));

        verify(tableService).getTable(tableId);
    }

    @Test
    @DisplayName("단일 테이블 조회 실패 - 존재하지 않는 테이블")
    void getTable_실패_테이블없음() throws Exception {
        // given
        when(tableService.getTable(tableId))
                .thenThrow(new SchemaException(ErrorCode.TABLE_NOT_FOUND));

        // when & then
        mockMvc.perform(get("/schemas/{schemaId}/tables/{tableId}", schemaId, tableId))
                .andExpect(status().isNotFound());

        verify(tableService).getTable(tableId);
    }

    @Test
    @DisplayName("테이블 비활성화 성공")
    void deactivateTable_성공() throws Exception {
        // given
        doNothing().when(tableService).deactivateTable(tableId);

        // when & then
        mockMvc.perform(delete("/schemas/{schemaId}/tables/{tableId}", schemaId, tableId))
                .andExpect(status().isOk());

        verify(tableService).deactivateTable(tableId);
    }

    @Test
    @DisplayName("테이블 비활성화 실패 - 존재하지 않는 테이블")
    void deactivateTable_실패_테이블없음() throws Exception {
        // given
        doThrow(new SchemaException(ErrorCode.TABLE_NOT_FOUND))
                .when(tableService).deactivateTable(tableId);

        // when & then
        mockMvc.perform(delete("/schemas/{schemaId}/tables/{tableId}", schemaId, tableId))
                .andExpect(status().isNotFound());

        verify(tableService).deactivateTable(tableId);
    }
}