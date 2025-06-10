package com.pandaterry.quota_microservice.integration;

import com.pandaterry.msa_contracts.constants.RoutePath;
import com.pandaterry.msa_contracts.constants.HeaderKeys;
import com.pandaterry.msa_contracts.dto.quota.request.QuotaUsageRecordRequest;
import com.pandaterry.msa_contracts.dto.quota.response.QuotaOrgResponse;
import com.pandaterry.quota_microservice.config.TestSecurityConfig;
import com.pandaterry.quota_microservice.domain.entity.Organization;
import com.pandaterry.quota_microservice.domain.entity.Plan;
import com.pandaterry.quota_microservice.domain.repository.OrganizationRepository;
import com.pandaterry.quota_microservice.domain.repository.PlanRepository;
import com.pandaterry.quota_microservice.integration.common.IntegrationTestBase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Import(TestSecurityConfig.class)
class QuotaIntegrationTest extends IntegrationTestBase {

    @Autowired
    private OrganizationRepository organizationRepository;

    @Autowired
    private PlanRepository planRepository;

    private UUID orgId;

    @BeforeEach
    void setUp() {
        Plan plan = Plan.create(UUID.randomUUID(), 100, 1);
        planRepository.save(plan);
        orgId = UUID.randomUUID();
        Organization organization = Organization.create(orgId, plan.getId());
        organizationRepository.save(organization);
    }

    @Test
    @DisplayName("초기 사용량은 0")
    void getQuota_initial_zero() throws Exception {
        MvcResult result = mockMvc.perform(get(RoutePath.Quota.ORG_ME)
                        .header(HeaderKeys.ORG_ID, orgId))
                .andExpect(status().isOk())
                .andReturn();

        QuotaOrgResponse response = objectMapper.readValue(
                result.getResponse().getContentAsString(),
                QuotaOrgResponse.class);

        assertThat(response.getTodayCount()).isZero();
        assertThat(response.getMonthCount()).isZero();
        assertThat(response.getRemainingQuota()).isEqualTo(100);
    }

    @Test
    @DisplayName("사용량 기록 후 조회")
    void record_and_query_usage() throws Exception {
        QuotaUsageRecordRequest request = QuotaUsageRecordRequest.builder()
                .orgId(orgId)
                .increment(3L)
                .build();

        mockMvc.perform(post(RoutePath.Quota.USAGE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNoContent());

        MvcResult result = mockMvc.perform(get(RoutePath.Quota.ORG_ME)
                        .header(HeaderKeys.ORG_ID, orgId))
                .andExpect(status().isOk())
                .andReturn();

        QuotaOrgResponse response = objectMapper.readValue(
                result.getResponse().getContentAsString(),
                QuotaOrgResponse.class);

        assertThat(response.getTodayCount()).isEqualTo(3L);
        assertThat(response.getMonthCount()).isEqualTo(3L);
        assertThat(response.getRemainingQuota()).isEqualTo(97);
    }
}