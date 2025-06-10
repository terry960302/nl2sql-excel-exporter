package com.pandaterry.quota_microservice.unit.application.service;

import com.pandaterry.msa_contracts.dto.quota.response.QuotaOrgResponse;
import com.pandaterry.quota_microservice.application.service.QuotaService;
import com.pandaterry.quota_microservice.domain.entity.*;
import com.pandaterry.quota_microservice.domain.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class QuotaServiceTest {

    @Mock
    private QuotaUsageDailyRepository dailyRepository;
    @Mock
    private QuotaUsageMonthlyRepository monthlyRepository;
    @Mock
    private OrganizationRepository organizationRepository;
    @Mock
    private PlanRepository planRepository;

    @InjectMocks
    private QuotaService quotaService;

    private UUID orgId;
    private Plan plan;
    private Organization organization;

    @BeforeEach
    void setUp() {
        orgId = UUID.randomUUID();
        plan = Plan.create(UUID.randomUUID(), 100, 5);
        organization = Organization.create(orgId, plan.getId());
    }

    @Test
    void getQuotaForOrg_성공() {
        LocalDate today = LocalDate.now();
        String monthKey = YearMonth.now().format(DateTimeFormatter.ofPattern("yyyyMM"));
        QuotaUsageDaily daily = QuotaUsageDaily.create(orgId, today, 3L);
        QuotaUsageMonthly monthly = QuotaUsageMonthly.create(orgId, monthKey, 10L);

        when(dailyRepository.findById(QuotaUsageDailyKey.builder().orgId(orgId).dateKey(today).build())).thenReturn(Optional.of(daily));
        when(monthlyRepository.findById(QuotaUsageMonthlyKey.builder().orgId(orgId).monthKey(monthKey).build())).thenReturn(Optional.of(monthly));
        when(organizationRepository.findById(orgId)).thenReturn(Optional.of(organization));
        when(planRepository.findById(plan.getId())).thenReturn(Optional.of(plan));

        QuotaOrgResponse response = quotaService.getQuotaForOrg(orgId);

        assertThat(response.getTodayCount()).isEqualTo(3L);
        assertThat(response.getMonthCount()).isEqualTo(10L);
        assertThat(response.getMonthlyQuota()).isEqualTo(100);
        assertThat(response.getRemainingQuota()).isEqualTo(90);
    }

    @Test
    void recordQuotaUsage_신규레코드생성() {
        LocalDate today = LocalDate.now();
        String monthKey = YearMonth.now().format(DateTimeFormatter.ofPattern("yyyyMM"));
        when(dailyRepository.findById(QuotaUsageDailyKey.builder().orgId(orgId).dateKey(today).build())).thenReturn(Optional.empty());
        when(monthlyRepository.findById(QuotaUsageMonthlyKey.builder().orgId(orgId).monthKey(monthKey).build())).thenReturn(Optional.empty());

        quotaService.recordQuotaUsage(orgId, 2L);

        ArgumentCaptor<QuotaUsageDaily> dailyCaptor = ArgumentCaptor.forClass(QuotaUsageDaily.class);
        ArgumentCaptor<QuotaUsageMonthly> monthlyCaptor = ArgumentCaptor.forClass(QuotaUsageMonthly.class);

        verify(dailyRepository).save(dailyCaptor.capture());
        verify(monthlyRepository).save(monthlyCaptor.capture());

        assertThat(dailyCaptor.getValue().getCount()).isEqualTo(2L);
        assertThat(monthlyCaptor.getValue().getCount()).isEqualTo(2L);
    }

    @Test
    void consumeQuotaUsage_호출시_사용량증가() {
        LocalDate today = LocalDate.now();
        String monthKey = YearMonth.now().format(DateTimeFormatter.ofPattern("yyyyMM"));
        when(dailyRepository.findById(QuotaUsageDailyKey.builder().orgId(orgId).dateKey(today).build())).thenReturn(Optional.empty());
        when(monthlyRepository.findById(QuotaUsageMonthlyKey.builder().orgId(orgId).monthKey(monthKey).build())).thenReturn(Optional.empty());

        quotaService.consumeQuotaUsage(
                QuotaUsageRecordRequest.builder().orgId(orgId).increment(1L).build()
        );

        ArgumentCaptor<QuotaUsageDaily> dailyCaptor = ArgumentCaptor.forClass(QuotaUsageDaily.class);
        ArgumentCaptor<QuotaUsageMonthly> monthlyCaptor = ArgumentCaptor.forClass(QuotaUsageMonthly.class);

        verify(dailyRepository).save(dailyCaptor.capture());
        verify(monthlyRepository).save(monthlyCaptor.capture());

        assertThat(dailyCaptor.getValue().getCount()).isEqualTo(1L);
        assertThat(monthlyCaptor.getValue().getCount()).isEqualTo(1L);
    }
}