package com.pandaterry.auth_microservice.presentation.dto;

import com.pandaterry.auth_microservice.domain.entity.User;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserInfoResponse {
    private String userId;
    private String email;
    private OrganizationResponse organization;
    private String planName;
    private int totalQuota;
    private int usedQuota;
    private int remainingQuota;

    public static UserInfoResponse of(User user, QuotaInfo quotaInfo) {
        return UserInfoResponse.builder()
                .userId(user.getId().toString())
                .email(user.getEmail())
                .organization(OrganizationResponse.of(user.getOrganization()))
                .planName(user.getOrganization().getPlan().getName())
                .totalQuota(quotaInfo.getTotalQuota())
                .usedQuota(quotaInfo.getUsedQuota())
                .remainingQuota(quotaInfo.getRemainingQuota())
                .build();
    }
}