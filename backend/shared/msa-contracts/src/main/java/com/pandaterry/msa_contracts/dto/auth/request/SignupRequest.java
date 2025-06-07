package com.pandaterry.msa_contracts.dto.auth.request;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

/**
 * 예시:
 * SignupRequest request = SignupRequest.builder()
 *     .email("test@example.com")
 *     .password("password")
 *     .build();
 */
@Getter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class SignupRequest {
    @NonNull
    @NotBlank
    private String name;

    @NonNull
    @NotBlank
    private String email;

    @NonNull
    @NotBlank
    private String password;

}