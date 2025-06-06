package com.pandaterry.msa_contracts.dto.auth.response;

import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.ToString;

/**
 * 예시:
 * TokenResponse response = TokenResponse.builder()
 *     .accessToken("access")
 *     .refreshToken("refresh")
 *     .build();
 */
@Getter
@ToString
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class TokenResponse {
    @NonNull
    @NotBlank
    private final String accessToken;

    @NonNull
    @NotBlank
    private final String refreshToken;

    public static TokenResponse of(String accessToken, String refreshToken) {
        return TokenResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }
}