package com.pandaterry.schema_microservice.integration.alias;

import com.pandaterry.schema_microservice.domain.entity.Alias;
import com.pandaterry.schema_microservice.domain.enumerated.EnableStatus;
import com.pandaterry.schema_microservice.integration.common.IntegrationTestBase;
import com.pandaterry.schema_microservice.presentation.dto.AliasCreateRequest;
import com.pandaterry.schema_microservice.presentation.dto.AliasResponse;
import com.pandaterry.schema_microservice.presentation.dto.AliasUpdateRequest;

import jakarta.transaction.Transactional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Transactional
class AliasIntegrationTest extends IntegrationTestBase {

    private UUID columnId;
    private Alias testAlias;

    @BeforeEach
    void setUp() {
        columnId = UUID.randomUUID();
        testAlias = AliasTestDataFactory.createTestAlias(columnId);
    }

    @Test
    @DisplayName("별칭 생성 성공")
    void createAlias_성공() throws Exception {
        // given
        AliasCreateRequest request = AliasCreateRequest.of(
                columnId,
                "new-alias",
                "New Alias Description");

        // when
        MvcResult result = mockMvc.perform(post("/columns/{columnId}/aliases", columnId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andReturn();

        // then
        AliasResponse response = objectMapper.readValue(
                result.getResponse().getContentAsString(),
                AliasResponse.class);

        assertThat(response.getAliasName()).isEqualTo("new-alias");
        assertThat(response.getDescription()).isEqualTo("New Alias Description");
        assertThat(response.getIsEnabled()).isEqualTo(EnableStatus.ENABLED);
    }

    @Test
    @DisplayName("별칭 목록 조회 성공")
    void getAliases_성공() throws Exception {
        // given
        AliasCreateRequest request = AliasCreateRequest.of(
                columnId,
                "test-alias",
                "Test Alias Description");

        mockMvc.perform(post("/columns/{columnId}/aliases", columnId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        // when
        MvcResult result = mockMvc.perform(get("/columns/{columnId}/aliases", columnId))
                .andExpect(status().isOk())
                .andReturn();

        // then
        List<AliasResponse> response = objectMapper.readValue(
                result.getResponse().getContentAsString(),
                objectMapper.getTypeFactory().constructCollectionType(List.class, AliasResponse.class));

        assertThat(response).hasSize(1);
        assertThat(response.get(0).getAliasName()).isEqualTo("test-alias");
        assertThat(response.get(0).getDescription()).isEqualTo("Test Alias Description");
    }

    @Test
    @DisplayName("별칭 상세 조회 성공")
    void getAlias_성공() throws Exception {
        // given
        AliasCreateRequest request = AliasCreateRequest.of(
                columnId,
                "test-alias",
                "Test Alias Description");

        MvcResult createResult = mockMvc.perform(post("/columns/{columnId}/aliases", columnId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andReturn();

        AliasResponse createdAlias = objectMapper.readValue(
                createResult.getResponse().getContentAsString(),
                AliasResponse.class);

        // when
        MvcResult result = mockMvc.perform(get("/columns/{columnId}/aliases/{aliasId}", columnId, createdAlias.getId()))
                .andExpect(status().isOk())
                .andReturn();

        // then
        AliasResponse response = objectMapper.readValue(
                result.getResponse().getContentAsString(),
                AliasResponse.class);

        assertThat(response.getId()).isEqualTo(createdAlias.getId());
        assertThat(response.getAliasName()).isEqualTo("test-alias");
        assertThat(response.getDescription()).isEqualTo("Test Alias Description");
    }

    @Test
    @DisplayName("별칭 상세 조회 실패 - 존재하지 않는 별칭")
    void getAlias_실패_별칭없음() throws Exception {
        // when & then
        mockMvc.perform(get("/columns/{columnId}/aliases/{aliasId}", columnId, UUID.randomUUID()))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("별칭 수정 성공")
    void updateAlias_성공() throws Exception {
        // given
        AliasCreateRequest createRequest = AliasCreateRequest.of(
                columnId,
                "test-alias",
                "Test Alias Description");

        MvcResult createResult = mockMvc.perform(post("/columns/{columnId}/aliases", columnId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isOk())
                .andReturn();

        AliasResponse createdAlias = objectMapper.readValue(
                createResult.getResponse().getContentAsString(),
                AliasResponse.class);

        AliasUpdateRequest updateRequest = AliasUpdateRequest.of(
                "updated-alias",
                "Updated Description");

        // when
        MvcResult result = mockMvc.perform(put("/columns/{columnId}/aliases/{aliasId}", columnId, createdAlias.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andReturn();

        // then
        AliasResponse response = objectMapper.readValue(
                result.getResponse().getContentAsString(),
                AliasResponse.class);

        assertThat(response.getAliasName()).isEqualTo("updated-alias");
        assertThat(response.getDescription()).isEqualTo("Updated Description");
    }

    @Test
    @DisplayName("별칭 비활성화 성공")
    void deactivateAlias_성공() throws Exception {
        // given
        AliasCreateRequest request = AliasCreateRequest.of(
                columnId,
                "test-alias",
                "Test Alias Description");

        MvcResult createResult = mockMvc.perform(post("/columns/{columnId}/aliases", columnId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andReturn();

        AliasResponse createdAlias = objectMapper.readValue(
                createResult.getResponse().getContentAsString(),
                AliasResponse.class);

        // when
        mockMvc.perform(delete("/columns/{columnId}/aliases/{aliasId}", columnId, createdAlias.getId()))
                .andExpect(status().isOk());

        // then
        MvcResult result = mockMvc.perform(get("/columns/{columnId}/aliases", columnId))
                .andExpect(status().isOk())
                .andReturn();

        List<AliasResponse> response = objectMapper.readValue(
                result.getResponse().getContentAsString(),
                objectMapper.getTypeFactory().constructCollectionType(List.class, AliasResponse.class));

        assertThat(response).isEmpty();
    }
}