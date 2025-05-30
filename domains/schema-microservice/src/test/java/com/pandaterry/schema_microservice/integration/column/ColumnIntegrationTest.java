package com.pandaterry.schema_microservice.integration.column;

import com.pandaterry.schema_microservice.domain.entity.ColumnDefinition;
import com.pandaterry.schema_microservice.domain.enumerated.EnableStatus;
import com.pandaterry.schema_microservice.infrastructure.repository.ColumnDefinitionRepository;
import com.pandaterry.schema_microservice.integration.common.IntegrationTestBase;
import com.pandaterry.schema_microservice.presentation.dto.ColumnDefinitionResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MvcResult;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class ColumnIntegrationTest extends IntegrationTestBase {

    @Autowired
    private ColumnDefinitionRepository columnDefinitionRepository;

    private UUID tableId;
    private ColumnDefinition testColumn;

    @BeforeEach
    void setUp() {
        tableId = UUID.randomUUID();
        testColumn = ColumnTestDataFactory.createTestColumn(tableId);
        columnDefinitionRepository.save(testColumn);
    }

    @Test
    void getColumnsByTable_성공() throws Exception {
        // when
        MvcResult result = mockMvc.perform(get("/tables/{tableId}/columns", tableId))
                .andExpect(status().isOk())
                .andReturn();

        // then
        List<ColumnDefinitionResponse> response = objectMapper.readValue(
                result.getResponse().getContentAsString(),
                objectMapper.getTypeFactory().constructCollectionType(List.class, ColumnDefinitionResponse.class));

        assertThat(response).hasSize(1);
        assertThat(response.get(0).getColumnName()).isEqualTo("test_column");
        assertThat(response.get(0).getTableId()).isEqualTo(tableId);
    }

    @Test
    void getColumn_성공() throws Exception {
        // when
        MvcResult result = mockMvc.perform(get("/tables/{tableId}/columns/{columnId}", tableId, testColumn.getId()))
                .andExpect(status().isOk())
                .andReturn();

        // then
        ColumnDefinitionResponse response = objectMapper.readValue(
                result.getResponse().getContentAsString(),
                ColumnDefinitionResponse.class);

        assertThat(response.getColumnName()).isEqualTo("test_column");
        assertThat(response.getTableId()).isEqualTo(tableId);
    }

    @Test
    void getColumn_실패_컬럼없음() throws Exception {
        // when & then
        mockMvc.perform(get("/tables/{tableId}/columns/{columnId}", tableId, UUID.randomUUID()))
                .andExpect(status().isNotFound());
    }

    @Test
    void deactivateColumn_성공() throws Exception {
        // when
        mockMvc.perform(delete("/tables/{tableId}/columns/{columnId}", tableId, testColumn.getId()))
                .andExpect(status().isOk());

        // then
        ColumnDefinition deactivatedColumn = columnDefinitionRepository.findById(testColumn.getId()).orElseThrow();
        assertThat(deactivatedColumn.getIsEnabled()).isEqualTo(EnableStatus.DISABLED);
    }

    @Test
    void deactivateColumn_실패_컬럼없음() throws Exception {
        // when & then
        mockMvc.perform(delete("/tables/{tableId}/columns/{columnId}", tableId, UUID.randomUUID()))
                .andExpect(status().isNotFound());
    }

    @Test
    void getColumnsByTable_비활성화된컬럼제외() throws Exception {
        // given
        testColumn.deactivate();
        columnDefinitionRepository.save(testColumn);

        // when
        MvcResult result = mockMvc.perform(get("/tables/{tableId}/columns", tableId))
                .andExpect(status().isOk())
                .andReturn();

        // then
        List<ColumnDefinitionResponse> response = objectMapper.readValue(
                result.getResponse().getContentAsString(),
                objectMapper.getTypeFactory().constructCollectionType(List.class, ColumnDefinitionResponse.class));

        assertThat(response).isEmpty();
    }
}