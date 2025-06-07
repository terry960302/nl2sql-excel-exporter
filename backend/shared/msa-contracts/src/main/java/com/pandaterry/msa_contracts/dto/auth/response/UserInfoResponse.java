package com.pandaterry.msa_contracts.dto.auth.response;

import lombok.*;

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
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class UserInfoResponse {
    @NonNull
    private String userId;

    @NonNull
    private String email;

    private OrganizationResponse organization;

    private String planName;

    private int totalQuota;

    private int usedQuota;

    private int remainingQuota;
}