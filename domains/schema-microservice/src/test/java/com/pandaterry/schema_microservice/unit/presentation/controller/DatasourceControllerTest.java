package com.pandaterry.schema_microservice.unit.presentation.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pandaterry.schema_microservice.application.service.DatasourceService;
import com.pandaterry.schema_microservice.domain.enumerated.DatabaseEngineType;
import com.pandaterry.schema_microservice.domain.enumerated.DatabaseType;
import com.pandaterry.schema_microservice.domain.enumerated.EnableStatus;
import com.pandaterry.schema_microservice.domain.exception.ErrorCode;
import com.pandaterry.schema_microservice.domain.exception.SchemaException;
import com.pandaterry.schema_microservice.presentation.controller.DatasourceController;
import com.pandaterry.schema_microservice.presentation.dto.DatasourceCreateRequest;
import com.pandaterry.schema_microservice.presentation.dto.DatasourceResponse;
import com.pandaterry.schema_microservice.presentation.dto.DatasourceUpdateRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(DatasourceController.class)
class DatasourceControllerTest {

        @Autowired
        private MockMvc mockMvc;

        @Autowired
        private ObjectMapper objectMapper;

        @MockitoBean
        private DatasourceService datasourceService;

        private UUID orgId;
        private UUID datasourceId;
        private DatasourceResponse testDatasourceResponse;

        @BeforeEach
        void setUp() {
                orgId = UUID.randomUUID();
                datasourceId = UUID.randomUUID();

                testDatasourceResponse = DatasourceResponse.builder()
                                .id(datasourceId)
                                .name("Test Datasource")
                                .dbType(DatabaseType.RDB)
                                .engineType(DatabaseEngineType.POSTGRESQL)
                                .endpoint("localhost:5432")
                                .encryptedPassword("encrypted_password")
                                .sslEnabled(true)
                                .isEnabled(EnableStatus.ENABLED)
                                .build();
        }

        @Test
        @DisplayName("데이터소스 생성 성공")
        void createDatasource_성공() throws Exception {
                // given
                DatasourceCreateRequest request = DatasourceCreateRequest.of(
                                "Test Datasource",
                                DatabaseType.RDB,
                                DatabaseEngineType.POSTGRESQL,
                                "localhost:5432",
                                "user",
                                "password",
                                true,
                                null);

                when(datasourceService.createDatasource(any(), any())).thenReturn(testDatasourceResponse);

                // when & then
                mockMvc.perform(post("/datasources")
                                .header("X-Organization-Id", orgId.toString())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.id").value(datasourceId.toString()))
                                .andExpect(jsonPath("$.name").value("Test Datasource"))
                                .andExpect(jsonPath("$.dbType").value("RDB"));

                verify(datasourceService).createDatasource(any(), any());
        }

        @Test
        @DisplayName("데이터소스 생성 실패 - orgId 헤더 누락")
        void createDatasource_실패_orgId헤더누락() throws Exception {
                // given
                DatasourceCreateRequest request = DatasourceCreateRequest.of(
                                "Test Datasource",
                                DatabaseType.RDB,
                                DatabaseEngineType.POSTGRESQL,
                                "localhost:5432",
                                "user",
                                "password",
                                true,
                                null);

                // when & then
                mockMvc.perform(post("/datasources")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                                .andExpect(status().isBadRequest());

                verify(datasourceService, never()).createDatasource(any(), any());
        }

        @Test
        @DisplayName("데이터소스 목록 조회 성공")
        void getDatasources_성공() throws Exception {
                // given
                List<DatasourceResponse> datasources = Arrays.asList(testDatasourceResponse);
                when(datasourceService.getDatasources(orgId)).thenReturn(datasources);

                // when & then
                mockMvc.perform(get("/datasources")
                                .header("X-Organization-Id", orgId.toString()))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$[0].id").value(datasourceId.toString()))
                                .andExpect(jsonPath("$[0].name").value("Test Datasource"));

                verify(datasourceService).getDatasources(orgId);
        }

        @Test
        @DisplayName("데이터소스 목록 조회 실패 - orgId 헤더 누락")
        void getDatasources_실패_orgId헤더누락() throws Exception {
                // when & then
                mockMvc.perform(get("/datasources"))
                                .andExpect(status().isBadRequest());

                verify(datasourceService, never()).getDatasources(any());
        }

        @Test
        @DisplayName("단일 데이터소스 조회 성공")
        void getDatasource_성공() throws Exception {
                // given
                when(datasourceService.getDatasource(datasourceId)).thenReturn(testDatasourceResponse);

                // when & then
                mockMvc.perform(get("/datasources/{datasourceId}", datasourceId))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.id").value(datasourceId.toString()))
                                .andExpect(jsonPath("$.name").value("Test Datasource"));

                verify(datasourceService).getDatasource(datasourceId);
        }

        @Test
        @DisplayName("단일 데이터소스 조회 실패 - 존재하지 않는 데이터소스")
        void getDatasource_실패_데이터소스없음() throws Exception {
                // given
                when(datasourceService.getDatasource(datasourceId))
                                .thenThrow(new SchemaException(ErrorCode.DATASOURCE_NOT_FOUND));

                // when & then
                mockMvc.perform(get("/datasources/{datasourceId}", datasourceId))
                                .andExpect(status().isNotFound());

                verify(datasourceService).getDatasource(datasourceId);
        }

        @Test
        @DisplayName("데이터소스 수정 성공")
        void updateDatasource_성공() throws Exception {
                // given
                DatasourceUpdateRequest request = new DatasourceUpdateRequest(
                                "Updated Datasource",
                                "localhost:5433",
                                "new_user",
                                "new_password",
                                true,
                                null);

                when(datasourceService.updateDatasource(any(), any())).thenReturn(testDatasourceResponse);

                // when & then
                mockMvc.perform(put("/datasources/{datasourceId}", datasourceId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.id").value(datasourceId.toString()))
                                .andExpect(jsonPath("$.name").value("Test Datasource"));

                verify(datasourceService).updateDatasource(eq(datasourceId), any());
        }

        @Test
        @DisplayName("데이터소스 수정 실패 - 존재하지 않는 데이터소스")
        void updateDatasource_실패_데이터소스없음() throws Exception {
                // given
                DatasourceUpdateRequest request = new DatasourceUpdateRequest(
                                "Updated Datasource",
                                "localhost:5433",
                                "new_user",
                                "new_password",
                                true,
                                null);

                when(datasourceService.updateDatasource(any(), any()))
                                .thenThrow(new SchemaException(ErrorCode.DATASOURCE_NOT_FOUND));

                // when & then
                mockMvc.perform(put("/datasources/{datasourceId}", datasourceId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                                .andExpect(status().isNotFound());

                verify(datasourceService).updateDatasource(eq(datasourceId), any());
        }

        @Test
        @DisplayName("데이터소스 비활성화 성공")
        void deactivateDatasource_성공() throws Exception {
                // given
                doNothing().when(datasourceService).deactivateDatasource(datasourceId);

                // when & then
                mockMvc.perform(delete("/datasources/{datasourceId}", datasourceId))
                                .andExpect(status().isOk());

                verify(datasourceService).deactivateDatasource(datasourceId);
        }

        @Test
        @DisplayName("데이터소스 비활성화 실패 - 존재하지 않는 데이터소스")
        void deactivateDatasource_실패_데이터소스없음() throws Exception {
                // given
                doThrow(new SchemaException(ErrorCode.DATASOURCE_NOT_FOUND))
                                .when(datasourceService).deactivateDatasource(datasourceId);

                // when & then
                mockMvc.perform(delete("/datasources/{datasourceId}", datasourceId))
                                .andExpect(status().isNotFound());

                verify(datasourceService).deactivateDatasource(datasourceId);
        }
}