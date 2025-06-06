package com.pandaterry.msa_contracts.dto.auth.response;

import java.time.LocalDateTime;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.ToString;

/**
 * 예시:
 * OrganizationResponse response = OrganizationResponse.builder()
 *     .id("org-id")
 *     .name("org")
 *     .planName("BASIC")
 *     .createdAt(LocalDateTime.now())
 *     .build();
 */
@Getter
@ToString
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class OrganizationResponse {
    @NonNull
    private final String id;

    @NonNull
    private final String name;

    private final String displayName;

    private final String planName;

    @NonNull
    private final LocalDateTime createdAt;

}
