package com.pandaterry.schema_microservice.unit.presentation.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pandaterry.msa_contracts.dto.schema.request.DatasourceUpdateRequest;
import com.pandaterry.schema_microservice.application.service.DatasourceService;
import com.pandaterry.schema_microservice.presentation.controller.DatasourceController;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(DatasourceController.class)
class DatasourceControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private DatasourceService datasourceService;

    @Test
    @DisplayName("헤더 누락 시 400 반환")
    void initDatasource_헤더없음_실패() throws Exception {
        mockMvc.perform(post("/datasources"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("정상 호출 시 서비스 실행")
    void initDatasource_성공() throws Exception {
        mockMvc.perform(post("/datasources")
                        .header("X-Organization-Id", UUID.randomUUID())
                        .header("X-User-Id", UUID.randomUUID())
                        .header("X-Agent-Id", UUID.randomUUID()))
                .andExpect(status().isOk());

        verify(datasourceService).initDatasource(any(), any(), any());
    }

    @Test
    @DisplayName("활성화 호출 시 서비스 실행")
    void activateDatasource_성공() throws Exception {
        DatasourceUpdateRequest request = new DatasourceUpdateRequest();
        UUID datasourceId = UUID.randomUUID();
        mockMvc.perform(put("/datasources/" + datasourceId)
                        .header("X-Organization-Id", UUID.randomUUID())
                        .header("X-User-Id", UUID.randomUUID())
                        .header("X-Agent-Id", UUID.randomUUID())
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        verify(datasourceService).activateDatasource(eq(datasourceId), any(), any(), any(), any());
    }
}
