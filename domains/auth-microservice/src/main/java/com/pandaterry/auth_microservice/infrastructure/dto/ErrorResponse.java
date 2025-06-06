package com.pandaterry.auth_microservice.infrastructure.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.ToString;

/**
 * 예시:
 * ErrorResponse response = ErrorResponse.builder()
 *     .code("ERR")
 *     .message("에러")
 *     .build();
 */
@Getter
@ToString
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ErrorResponse {
    @NonNull
    private final String code;

    @NonNull
    private final String message;
}