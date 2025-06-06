package com.pandaterry.msa_contracts.dto.auth.request;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

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
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class LoginRequest {
    @NonNull
    @NotBlank
    private String email;

    @NonNull
    @NotBlank
    private String password;
}