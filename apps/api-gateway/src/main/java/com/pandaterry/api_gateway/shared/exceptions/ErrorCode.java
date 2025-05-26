package com.pandaterry.api_gateway.shared.exceptions;

public enum ErrorCode {

    // 공통
    JWT_EXPIRED(401, "COMMON_01", "jwt 토큰이 만료되었습니다."),
    ;


    private final int status;
    private final String code;
    private final String message;

    ErrorCode(final int status, final String code, final String message) {
        this.status = status;
        this.code = code;
        this.message = message;
    }
}
