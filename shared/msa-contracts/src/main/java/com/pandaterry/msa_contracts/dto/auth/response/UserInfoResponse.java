package com.pandaterry.msa_contracts.dto.auth.response;


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
}