package com.pandaterry.msa_contracts.dto.auth.response;

import java.time.LocalDateTime;

import lombok.*;

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
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class OrganizationResponse {
    @NonNull
    private String id;

    @NonNull
    private String name;

    private String displayName;

    private String planName;

    private LocalDateTime createdAt;

}
