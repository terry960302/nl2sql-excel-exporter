package com.pandaterry.quota_microservice.presentation.controller;

import com.pandaterry.msa_contracts.dto.quota.request.QuotaUsageRecordRequest;
import com.pandaterry.msa_contracts.dto.quota.response.QuotaOrgResponse;
import com.pandaterry.msa_contracts.dto.quota.response.QuotaOrgDetailResponse;
import com.pandaterry.msa_contracts.dto.quota.response.QuotaOrgsPageResponse;
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

    @GetMapping("/organizations/me")
    public ResponseEntity<QuotaOrgResponse> getMyOrgQuota(@RequestHeader("X-Organization-Id") UUID orgId) {
        return ResponseEntity.ok(quotaService.getQuotaForOrg(orgId));
    }

    // ADMIN
    @GetMapping("/organizations")
    public ResponseEntity<QuotaOrgsPageResponse> getAllQuota(@RequestParam(defaultValue = "0") int page,
                                                             @RequestParam(defaultValue = "50") int size) {
        return ResponseEntity.ok(quotaService.getAllQuotaStatus(PageRequest.of(page, size)));
    }

    // ADMIN
    @GetMapping("/organizations/{orgId}")
    public ResponseEntity<QuotaOrgDetailResponse> getQuotaDetail(@PathVariable UUID orgId,
                                                                 @RequestParam String startDate,
                                                                 @RequestParam String endDate) {
        LocalDate start = LocalDate.parse(startDate);
        LocalDate end = LocalDate.parse(endDate);
        return ResponseEntity.ok(quotaService.getQuotaDetailForOrg(orgId, start, end));
    }

    // TODO: 컨트롤러 호출은 불가피한 경우에, 일반적으론 쿼리서비스에서 이벤트 기반 호출
    @PostMapping("/usage")
    public ResponseEntity<Void> recordUsage(@RequestBody QuotaUsageRecordRequest request) {
        quotaService.recordQuotaUsage(request.getOrgId(), request.getIncrement());
        return ResponseEntity.noContent().build();
    }
}
