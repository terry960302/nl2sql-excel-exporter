package com.pandaterry.gateway.shared.exceptions;

public enum ErrorCode {

    // 공통
    JWT_EXPIRED(401, "COMMON_01", "jwt 토큰이 만료되었습니다."),
    SERVICE_UNAVAILABLE(500, "COMMON_02", "서비스가 일시적으로 사용불가 상태입니다.")
    ;


    private final int status;
    private final String code;
    private final String message;

    ErrorCode(final int status, final String code, final String message) {
        this.status = status;
        this.code = code;
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
