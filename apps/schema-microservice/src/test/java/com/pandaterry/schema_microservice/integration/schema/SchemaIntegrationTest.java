package com.pandaterry.schema_microservice.integration.schema;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pandaterry.schema_microservice.domain.entity.Datasource;
import com.pandaterry.schema_microservice.domain.entity.Schema;
import com.pandaterry.schema_microservice.domain.enumerated.EnableStatus;
import com.pandaterry.schema_microservice.infrastructure.repository.DatasourceRepository;
import com.pandaterry.schema_microservice.infrastructure.repository.SchemaRepository;
import com.pandaterry.schema_microservice.integration.common.IntegrationTestBase;
import com.pandaterry.schema_microservice.presentation.dto.SchemaCreateRequest;
import com.pandaterry.schema_microservice.presentation.dto.SchemaResponse;
import com.pandaterry.schema_microservice.presentation.dto.SchemaUpdateRequest;
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
class SchemaIntegrationTest extends IntegrationTestBase {

    @Autowired
    private DatasourceRepository datasourceRepository;

    @Autowired
    private SchemaRepository schemaRepository;

    @Autowired
    private SchemaTestDataFactory schemaTestDataFactory;

    @Autowired
    private ObjectMapper objectMapper;

    private UUID orgId;
    private UUID userId;
    private Datasource testDatasource;
    private Schema testSchema;

    @BeforeEach
    void setUp() {
        orgId = UUID.randomUUID();
        userId = UUID.randomUUID();
        testDatasource = schemaTestDataFactory.createDatasource();
        datasourceRepository.save(testDatasource);
        testSchema = schemaTestDataFactory.createSchema(testDatasource);
    }

    @Test
    @DisplayName("스키마 생성 성공")
    void createSchema_성공() throws Exception {
        // given
        SchemaCreateRequest request = SchemaCreateRequest.of(testDatasource.getId(), "New Schema");

        // when
        MvcResult result = mockMvc.perform(post("/schemas")
                .header("X-Organization-Id", orgId)
                .header("X-User-Id", userId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andReturn();

        // then
        SchemaResponse response = objectMapper.readValue(
                result.getResponse().getContentAsString(),
                SchemaResponse.class);

        assertThat(response.getName()).isEqualTo("New Schema");
        assertThat(response.getDatasourceId()).isEqualTo(testDatasource.getId());
        assertThat(response.getIsEnabled()).isEqualTo(EnableStatus.ENABLED);
    }

    @Test
    @DisplayName("스키마 생성 실패 - orgId 헤더 누락")
    void createSchema_실패_orgId헤더누락() throws Exception {
        // given
        SchemaCreateRequest request = SchemaCreateRequest.of(testDatasource.getId(), "New Schema");

        // when & then
        mockMvc.perform(post("/schemas")
                .header("X-User-Id", userId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("스키마 목록 조회 성공")
    void getSchemasByDatasource_성공() throws Exception {
        // when
        MvcResult result = mockMvc.perform(get("/schemas/datasource/{datasourceId}", testDatasource.getId()))
                .andExpect(status().isOk())
                .andReturn();

        // then
        List<SchemaResponse> response = objectMapper.readValue(
                result.getResponse().getContentAsString(),
                objectMapper.getTypeFactory().constructCollectionType(List.class, SchemaResponse.class));

        assertThat(response).hasSize(1);
        assertThat(response.get(0).getName()).isEqualTo("Test Schema");
        assertThat(response.get(0).getDatasourceId()).isEqualTo(testDatasource.getId());
    }

    @Test
    @DisplayName("스키마 상세 조회 성공")
    void getSchema_성공() throws Exception {
        // when
        MvcResult result = mockMvc.perform(get("/schemas/{schemaId}", testSchema.getId()))
                .andExpect(status().isOk())
                .andReturn();

        // then
        SchemaResponse response = objectMapper.readValue(
                result.getResponse().getContentAsString(),
                SchemaResponse.class);

        assertThat(response.getId()).isEqualTo(testSchema.getId());
        assertThat(response.getName()).isEqualTo("Test Schema");
        assertThat(response.getDatasourceId()).isEqualTo(testDatasource.getId());
    }

    @Test
    @DisplayName("스키마 상세 조회 실패 - 존재하지 않는 스키마")
    void getSchema_실패_스키마없음() throws Exception {
        // when & then
        mockMvc.perform(get("/schemas/{schemaId}", UUID.randomUUID()))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("스키마 수정 성공")
    void updateSchema_성공() throws Exception {
        // given
        SchemaUpdateRequest request = SchemaUpdateRequest.of("Updated Schema", EnableStatus.ENABLED);

        // when
        MvcResult result = mockMvc.perform(put("/schemas/{schemaId}", testSchema.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andReturn();

        // then
        SchemaResponse response = objectMapper.readValue(
                result.getResponse().getContentAsString(),
                SchemaResponse.class);

        assertThat(response.getName()).isEqualTo("Updated Schema");
        assertThat(response.getIsEnabled()).isEqualTo(EnableStatus.ENABLED);
    }

    @Test
    @DisplayName("스키마 비활성화 성공")
    void deactivateSchema_성공() throws Exception {
        // when & then
        mockMvc.perform(delete("/schemas/{schemaId}", testSchema.getId()))
                .andExpect(status().isOk());

        Schema deactivatedSchema = schemaRepository.findById(testSchema.getId()).orElseThrow();
        assertThat(deactivatedSchema.getIsEnabled()).isEqualTo(EnableStatus.DISABLED);
    }

    @Test
    @DisplayName("비활성화된 스키마는 목록에서 제외됨")
    void getSchemasByDatasource_비활성화된스키마제외() throws Exception {
        // given
        testSchema.deactivate();
        schemaRepository.save(testSchema);

        // when
        MvcResult result = mockMvc.perform(get("/schemas/datasource/{datasourceId}", testDatasource.getId()))
                .andExpect(status().isOk())
                .andReturn();

        // then
        List<SchemaResponse> response = objectMapper.readValue(
                result.getResponse().getContentAsString(),
                objectMapper.getTypeFactory().constructCollectionType(List.class, SchemaResponse.class));

        assertThat(response).isEmpty();
    }
}