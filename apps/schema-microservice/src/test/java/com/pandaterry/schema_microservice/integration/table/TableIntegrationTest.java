package com.pandaterry.schema_microservice.integration.table;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pandaterry.schema_microservice.domain.entity.Datasource;
import com.pandaterry.schema_microservice.domain.entity.Schema;
import com.pandaterry.schema_microservice.domain.entity.TableDefinition;
import com.pandaterry.schema_microservice.domain.enumerated.EnableStatus;
import com.pandaterry.schema_microservice.infrastructure.repository.DatasourceRepository;
import com.pandaterry.schema_microservice.infrastructure.repository.SchemaRepository;
import com.pandaterry.schema_microservice.infrastructure.repository.TableDefinitionRepository;
import com.pandaterry.schema_microservice.integration.common.IntegrationTestBase;
import com.pandaterry.schema_microservice.presentation.dto.TableDefinitionResponse;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Transactional
class TableIntegrationTest extends IntegrationTestBase {

    @Autowired
    private DatasourceRepository datasourceRepository;

    @Autowired
    private SchemaRepository schemaRepository;

    @Autowired
    private TableDefinitionRepository tableDefinitionRepository;

    @Autowired
    private TableTestDataFactory tableTestDataFactory;

    @Autowired
    private ObjectMapper objectMapper;

    private UUID orgId;
    private UUID userId;
    private Datasource testDatasource;
    private Schema testSchema;
    private TableDefinition testTable;

    @BeforeEach
    void setUp() {
        orgId = UUID.randomUUID();
        userId = UUID.randomUUID();
        testDatasource = tableTestDataFactory.createDatasource();
        datasourceRepository.save(testDatasource);
        testSchema = tableTestDataFactory.createSchema(testDatasource);
        schemaRepository.save(testSchema);
        testTable = tableTestDataFactory.createTable(testSchema);
    }

    @Test
    @DisplayName("테이블 목록 조회 성공")
    void getTablesBySchema_성공() throws Exception {
        // when
        MvcResult result = mockMvc.perform(get("/schemas/{schemaId}/tables", testSchema.getId()))
                .andExpect(status().isOk())
                .andReturn();

        // then
        List<TableDefinitionResponse> response = objectMapper.readValue(
                result.getResponse().getContentAsString(),
                objectMapper.getTypeFactory().constructCollectionType(List.class, TableDefinitionResponse.class));

        assertThat(response).hasSize(1);
        assertThat(response.get(0).getTableName()).isEqualTo("Test Table");
        assertThat(response.get(0).getSchemaId()).isEqualTo(testSchema.getId());
    }

    @Test
    @DisplayName("테이블 상세 조회 성공")
    void getTable_성공() throws Exception {
        // when
        MvcResult result = mockMvc
                .perform(get("/schemas/{schemaId}/tables/{tableId}", testSchema.getId(), testTable.getId()))
                .andExpect(status().isOk())
                .andReturn();

        // then
        TableDefinitionResponse response = objectMapper.readValue(
                result.getResponse().getContentAsString(),
                TableDefinitionResponse.class);

        assertThat(response.getId()).isEqualTo(testTable.getId());
        assertThat(response.getTableName()).isEqualTo("Test Table");
        assertThat(response.getSchemaId()).isEqualTo(testSchema.getId());
    }

    @Test
    @DisplayName("테이블 상세 조회 실패 - 존재하지 않는 테이블")
    void getTable_실패_테이블없음() throws Exception {
        // when & then
        mockMvc.perform(get("/schemas/{schemaId}/tables/{tableId}", testSchema.getId(), UUID.randomUUID()))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("테이블 비활성화 성공")
    void deactivateTable_성공() throws Exception {
        // when & then
        mockMvc.perform(delete("/schemas/{schemaId}/tables/{tableId}", testSchema.getId(), testTable.getId()))
                .andExpect(status().isOk());

        TableDefinition deactivatedTable = tableDefinitionRepository.findById(testTable.getId()).orElseThrow();
        assertThat(deactivatedTable.getIsEnabled()).isEqualTo(EnableStatus.DISABLED);
    }

    @Test
    @DisplayName("비활성화된 테이블은 목록에서 제외됨")
    void getTablesBySchema_비활성화된테이블제외() throws Exception {
        // given
        testTable.deactivate();
        tableDefinitionRepository.save(testTable);

        // when
        MvcResult result = mockMvc.perform(get("/schemas/{schemaId}/tables", testSchema.getId()))
                .andExpect(status().isOk())
                .andReturn();

        // then
        List<TableDefinitionResponse> response = objectMapper.readValue(
                result.getResponse().getContentAsString(),
                objectMapper.getTypeFactory().constructCollectionType(List.class, TableDefinitionResponse.class));

        assertThat(response).isEmpty();
    }
}