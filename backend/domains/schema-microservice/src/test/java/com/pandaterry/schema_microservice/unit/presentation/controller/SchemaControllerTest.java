package com.pandaterry.schema_microservice.unit.presentation.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pandaterry.msa_contracts.dto.schema.request.RegisterSchemaRequest;
import com.pandaterry.schema_microservice.application.service.SchemaService;
import com.pandaterry.schema_microservice.presentation.controller.SchemaController;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(SchemaController.class)
class SchemaControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private SchemaService schemaService;

    @Test
    @DisplayName("필수 헤더가 없으면 400 반환")
    void uploadSchema_헤더없음_실패() throws Exception {
        RegisterSchemaRequest req = new RegisterSchemaRequest(null, null, null, UUID.randomUUID(), "name", List.of(), "{}");
        mockMvc.perform(post("/v1/schemas")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("정상 호출 시 서비스 메서드 실행")
    void uploadSchema_성공() throws Exception {
        RegisterSchemaRequest req = new RegisterSchemaRequest(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), "name", List.of(), "{}");
        mockMvc.perform(post("/v1/schemas")
                        .header("X-Organization-Id", req.orgId())
                        .header("X-User-Id", req.userId())
                        .header("X-Agent-Id", req.agentId())
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk());

        verify(schemaService).uploadSchema(req.orgId(), req.userId(), req.agentId(), req);
    }
}
