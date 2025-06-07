package com.pandaterry.auth_microservice.presentation.mappers;

import com.pandaterry.auth_microservice.domain.entity.User;
import com.pandaterry.msa_contracts.dto.auth.response.OrganizationResponse;
import com.pandaterry.msa_contracts.dto.auth.response.QuotaInfo;
import com.pandaterry.msa_contracts.dto.auth.response.UserInfoResponse;

public class UserInfoMapper {
    public static UserInfoResponse toResponse(User user, QuotaInfo quotaInfo) {
        return UserInfoResponse.builder()
                .userId(user.getId().toString())
                .email(user.getEmail())
                .organization(OrganizationMapper.toResponse(user.getOrganization()))
                .planName(user.getOrganization().getPlan().getName())
                .totalQuota(quotaInfo.getTotalQuota())
                .usedQuota(quotaInfo.getUsedQuota())
                .remainingQuota(quotaInfo.getRemainingQuota())
                .build();
    }
}
