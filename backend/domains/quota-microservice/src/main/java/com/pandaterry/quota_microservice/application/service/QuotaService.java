package com.pandaterry.quota_microservice.application.service;

import com.pandaterry.msa_contracts.dto.quota.response.*;
import com.pandaterry.quota_microservice.domain.entity.*;
import com.pandaterry.msa_contracts.dto.quota.request.QuotaUsageRecordRequest;
import org.springframework.kafka.annotation.KafkaListener;
import com.pandaterry.quota_microservice.domain.exception.ErrorCode;
import com.pandaterry.quota_microservice.domain.exception.QuotaException;
import com.pandaterry.quota_microservice.domain.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class QuotaService {

    private final QuotaUsageDailyRepository dailyRepository;
    private final QuotaUsageMonthlyRepository monthlyRepository;
    private final OrganizationRepository organizationRepository;
    private final PlanRepository planRepository;

    @Transactional(readOnly = true)
    public QuotaOrgResponse getQuotaForOrg(UUID orgId) {
        LocalDate today = LocalDate.now();
        String monthKey = currentMonthKey();

        long todayCount = getDailyCount(orgId, today);
        long monthCount = getMonthlyCount(orgId, monthKey);
        Plan plan = loadPlan(orgId);
        int monthlyQuota = plan.getMonthlyQuota();

        return QuotaOrgResponse.builder()
                .todayCount(todayCount)
                .monthCount(monthCount)
                .monthlyQuota(monthlyQuota)
                .remainingQuota((int) (monthlyQuota - monthCount))
                .build();
    }

    @Transactional(readOnly = true)
    public QuotaOrgsPageResponse getAllQuotaStatus(Pageable pageable) {
        String monthKey = currentMonthKey();
        Page<Organization> page = organizationRepository.findAll(pageable);

        List<QuotaUsersItem> content = page.getContent().stream()
                .map(org -> toUsersItem(org, monthKey))
                .toList();

        return QuotaOrgsPageResponse.builder()
                .content(content)
                .page(page.getNumber())
                .size(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .build();
    }

    @Transactional(readOnly = true)
    public QuotaOrgDetailResponse getQuotaDetailForOrg(UUID orgId, LocalDate start, LocalDate end) {
        List<QuotaUserDetailDaily> dailyUsage = dailyRepository
                .findByIdOrgIdAndIdDateKeyBetweenOrderByIdDateKeyAsc(orgId, start, end)
                .stream()
                .map(d -> QuotaUserDetailDaily.builder()
                        .date(d.getId().getDateKey())
                        .count(d.getCount())
                        .build())
                .toList();

        String monthKey = currentMonthKey();
        long monthCount = getMonthlyCount(orgId, monthKey);
        Plan plan = loadPlan(orgId);

        QuotaUserDetailMonthly monthlyUsage = QuotaUserDetailMonthly.builder()
                .monthKey(monthKey)
                .count(monthCount)
                .monthlyQuota(plan.getMonthlyQuota())
                .build();

        return QuotaOrgDetailResponse.builder()
                .dailyUsage(dailyUsage)
                .monthlyUsage(monthlyUsage)
                .build();
    }

    public void recordQuotaUsage(UUID orgId, long increment) {
        LocalDate today = LocalDate.now();
        String monthKey = currentMonthKey();

        QuotaUsageDaily daily = getOrCreateDaily(orgId, today);
        daily.increase(increment);
        dailyRepository.save(daily);

        QuotaUsageMonthly monthly = getOrCreateMonthly(orgId, monthKey);
        monthly.increase(increment);
        monthlyRepository.save(monthly);
    }

    @KafkaListener(topics = "${quota.usage.topic:quota-usage}")
    public void consumeQuotaUsage(QuotaUsageRecordRequest request) {
        recordQuotaUsage(request.getOrgId(), request.getIncrement());
    }

    private String currentMonthKey() {
        return YearMonth.now().format(DateTimeFormatter.ofPattern("yyyyMM"));
    }

    private long getDailyCount(UUID orgId, LocalDate date) {
        return dailyRepository.findById(QuotaUsageDailyKey.builder().orgId(orgId).dateKey(date).build())
                .map(QuotaUsageDaily::getCount)
                .orElse(0L);
    }

    private long getMonthlyCount(UUID orgId, String monthKey) {
        return monthlyRepository.findById(QuotaUsageMonthlyKey.builder().orgId(orgId).monthKey(monthKey).build())
                .map(QuotaUsageMonthly::getCount)
                .orElse(0L);
    }

    private Plan loadPlan(UUID orgId) {
        Organization org = organizationRepository.findById(orgId)
                .orElseThrow(() -> new QuotaException(ErrorCode.ORGANIZATION_NOT_FOUND));
        return planRepository.findById(org.getPlanId())
                .orElseThrow(() -> new QuotaException(ErrorCode.PLAN_NOT_FOUND));
    }

    private QuotaUsersItem toUsersItem(Organization org, String monthKey) {
        Plan plan = planRepository.findById(org.getPlanId()).orElse(null);
        int monthlyQuota = plan != null ? plan.getMonthlyQuota() : 0;
        long monthCount = getMonthlyCount(org.getId(), monthKey);
        int remainingQuota = (int) (monthlyQuota - monthCount);

        return QuotaUsersItem.builder()
                .orgId(org.getId())
                .monthlyQuota(monthlyQuota)
                .monthCount(monthCount)
                .remainingQuota(remainingQuota)
                .build();
    }

    private QuotaUsageDaily getOrCreateDaily(UUID orgId, LocalDate date) {
        return dailyRepository.findById(QuotaUsageDailyKey.builder().orgId(orgId).dateKey(date).build())
                .orElseGet(() -> QuotaUsageDaily.create(orgId, date, 0L));
    }

    private QuotaUsageMonthly getOrCreateMonthly(UUID orgId, String monthKey) {
        return monthlyRepository.findById(QuotaUsageMonthlyKey.builder().orgId(orgId).monthKey(monthKey).build())
                .orElseGet(() -> QuotaUsageMonthly.create(orgId, monthKey, 0L));
    }
}
