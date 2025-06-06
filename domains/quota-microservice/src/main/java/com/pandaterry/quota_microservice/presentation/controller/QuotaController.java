package com.pandaterry.quota_microservice.presentation.controller;

import com.pandaterry.msa_contracts.dto.quota.request.QuotaUsageRecordRequest;
import com.pandaterry.msa_contracts.dto.quota.response.QuotaMeResponse;
import com.pandaterry.msa_contracts.dto.quota.response.QuotaUserDetailResponse;
import com.pandaterry.msa_contracts.dto.quota.response.QuotaUsersPageResponse;
import com.pandaterry.quota_microservice.application.service.QuotaService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.UUID;

@RestController
@RequestMapping("/quota")
@RequiredArgsConstructor
public class QuotaController {

    private final QuotaService quotaService;

    @GetMapping("/me")
    public ResponseEntity<QuotaMeResponse> getMyQuota(@RequestHeader("X-Organization-Id") UUID orgId) {
        return ResponseEntity.ok(quotaService.getQuotaForOrg(orgId));
    }

    @GetMapping("/users")
    public ResponseEntity<QuotaUsersPageResponse> getAllQuota(@RequestParam(defaultValue = "0") int page,
                                                              @RequestParam(defaultValue = "50") int size) {
        return ResponseEntity.ok(quotaService.getAllQuotaStatus(PageRequest.of(page, size)));
    }

    @GetMapping("/users/{orgId}")
    public ResponseEntity<QuotaUserDetailResponse> getQuotaDetail(@PathVariable UUID orgId,
                                                                  @RequestParam String startDate,
                                                                  @RequestParam String endDate) {
        LocalDate start = LocalDate.parse(startDate);
        LocalDate end = LocalDate.parse(endDate);
        return ResponseEntity.ok(quotaService.getQuotaDetailForOrg(orgId, start, end));
    }

    @PostMapping("/usage")
    public ResponseEntity<Void> recordUsage(@RequestBody QuotaUsageRecordRequest request) {
        quotaService.recordQuotaUsage(request.getOrgId(), request.getIncrement());
        return ResponseEntity.noContent().build();
    }
}
