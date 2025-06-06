package com.pandaterry.msa_contracts.dto.auth.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.ToString;

/**
 * 예시:
 * LoginRequest request = LoginRequest.builder()
 *     .email("test@example.com")
 *     .password("password")
 *     .build();
 */
@Getter
@ToString
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class LoginRequest {
    @NonNull
    @NotBlank
    private final String email;

    @NonNull
    @NotBlank
    private final String password;
}