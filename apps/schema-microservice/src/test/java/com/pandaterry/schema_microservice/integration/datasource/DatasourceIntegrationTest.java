package com.pandaterry.schema_microservice.integration.datasource;

import com.pandaterry.schema_microservice.domain.entity.Datasource;
import com.pandaterry.schema_microservice.domain.enumerated.DatabaseEngineType;
import com.pandaterry.schema_microservice.domain.enumerated.DatabaseType;
import com.pandaterry.schema_microservice.domain.enumerated.EnableStatus;
import com.pandaterry.schema_microservice.infrastructure.repository.DatasourceRepository;
import com.pandaterry.schema_microservice.integration.common.IntegrationTestBase;
import com.pandaterry.schema_microservice.presentation.dto.DatasourceCreateRequest;
import com.pandaterry.schema_microservice.presentation.dto.DatasourceResponse;
import com.pandaterry.schema_microservice.presentation.dto.DatasourceUpdateRequest;

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
class DatasourceIntegrationTest extends IntegrationTestBase {

    @Autowired
    private DatasourceRepository datasourceRepository;

    private UUID orgId;
    private Datasource testDatasource;

    @BeforeEach
    void setUp() {
        orgId = UUID.randomUUID();
        testDatasource = DatasourceTestDataFactory.createTestDatasource(orgId);
        datasourceRepository.save(testDatasource);
    }

    @Test
    @DisplayName("데이터소스 생성 성공")
    void createDatasource_성공() throws Exception {
        // given
        DatasourceCreateRequest request = DatasourceCreateRequest.builder()
                .name("new-datasource")
                .dbType(DatabaseType.RDB)
                .engineType(DatabaseEngineType.POSTGRESQL)
                .endpoint("localhost:5432")
                .username("test_user")
                .password("test_password")
                .sslEnabled(true)
                .build();

        // when
        MvcResult result = mockMvc.perform(post("/datasources")
                .header("X-Organization-Id", orgId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andReturn();

        // then
        DatasourceResponse response = objectMapper.readValue(
                result.getResponse().getContentAsString(),
                DatasourceResponse.class);

        assertThat(response.getName()).isEqualTo("new-datasource");
        assertThat(response.getDbType()).isEqualTo(DatabaseType.RDB);
        assertThat(response.getEngineType()).isEqualTo(DatabaseEngineType.POSTGRESQL);
        assertThat(response.getIsEnabled()).isEqualTo(EnableStatus.ENABLED);
    }

    @Test
    @DisplayName("데이터소스 목록 조회 성공")
    void getDatasources_성공() throws Exception {
        // when
        MvcResult result = mockMvc.perform(get("/datasources")
                .header("X-Organization-Id", orgId))
                .andExpect(status().isOk())
                .andReturn();

        // then
        List<DatasourceResponse> response = objectMapper.readValue(
                result.getResponse().getContentAsString(),
                objectMapper.getTypeFactory().constructCollectionType(List.class, DatasourceResponse.class));

        assertThat(response).hasSize(1);
        assertThat(response.get(0).getName()).isEqualTo("test-datasource");
        assertThat(response.get(0).getDbType()).isEqualTo(DatabaseType.RDB);
    }

    @Test
    @DisplayName("데이터소스 상세 조회 성공")
    void getDatasource_성공() throws Exception {
        // when
        MvcResult result = mockMvc.perform(get("/datasources/{datasourceId}", testDatasource.getId()))
                .andExpect(status().isOk())
                .andReturn();

        // then
        DatasourceResponse response = objectMapper.readValue(
                result.getResponse().getContentAsString(),
                DatasourceResponse.class);

        assertThat(response.getId()).isEqualTo(testDatasource.getId());
        assertThat(response.getName()).isEqualTo("test-datasource");
        assertThat(response.getDbType()).isEqualTo(DatabaseType.RDB);
    }

    @Test
    @DisplayName("데이터소스 상세 조회 실패 - 존재하지 않는 데이터소스")
    void getDatasource_실패_데이터소스없음() throws Exception {
        // when & then
        mockMvc.perform(get("/datasources/{datasourceId}", UUID.randomUUID()))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("데이터소스 수정 성공")
    void updateDatasource_성공() throws Exception {
        // given
        DatasourceUpdateRequest request = DatasourceUpdateRequest.of("updated-datasource", "localhost:5433", "test_user", "test_password", true, null);

        // when
        MvcResult result = mockMvc.perform(put("/datasources/{datasourceId}", testDatasource.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andReturn();

        // then
        DatasourceResponse response = objectMapper.readValue(
                result.getResponse().getContentAsString(),
                DatasourceResponse.class);

        assertThat(response.getName()).isEqualTo("updated-datasource");
        assertThat(response.getEndpoint()).isEqualTo("localhost:5433");
    }

    @Test
    @DisplayName("데이터소스 비활성화 성공")
    void deactivateDatasource_성공() throws Exception {
        // when
        mockMvc.perform(delete("/datasources/{datasourceId}", testDatasource.getId()))
                .andExpect(status().isOk());

        // then
        Datasource deactivatedDatasource = datasourceRepository.findById(testDatasource.getId()).orElseThrow();
        assertThat(deactivatedDatasource.getIsEnabled()).isEqualTo(EnableStatus.DISABLED);
    }

    @Test
    @DisplayName("비활성화된 데이터소스는 목록에서 제외됨")
    void getDatasources_비활성화된데이터소스제외() throws Exception {
        // given
        testDatasource.deactivate();
        datasourceRepository.save(testDatasource);

        // when
        MvcResult result = mockMvc.perform(get("/datasources")
                .header("X-Organization-Id", orgId))
                .andExpect(status().isOk())
                .andReturn();

        // then
        List<DatasourceResponse> response = objectMapper.readValue(
                result.getResponse().getContentAsString(),
                objectMapper.getTypeFactory().constructCollectionType(List.class, DatasourceResponse.class));

        assertThat(response).isEmpty();
    }
}