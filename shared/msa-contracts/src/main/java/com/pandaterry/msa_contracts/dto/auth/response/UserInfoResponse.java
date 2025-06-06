package com.pandaterry.msa_contracts.dto.auth.response;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.ToString;

/**
 * 예시:
 * UserInfoResponse info = UserInfoResponse.builder()
 *     .userId("user")
 *     .email("user@example.com")
 *     .build();
 */
@Getter
@ToString
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class UserInfoResponse {
    @NonNull
    private final String userId;

    @NonNull
    private final String email;

    private final OrganizationResponse organization;

    private final String planName;

    private final int totalQuota;

    private final int usedQuota;

    private final int remainingQuota;
}