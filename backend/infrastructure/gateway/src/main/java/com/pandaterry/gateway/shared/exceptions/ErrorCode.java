package com.pandaterry.gateway.shared.exceptions;

public enum ErrorCode {

    // 공통
    UNKNOWN_ERROR(500, "COMMON_00","서버 내부 오류가 발생했습니다."),
    JWT_EXPIRED(401, "COMMON_01", "jwt 토큰이 만료되었습니다."),
    SERVICE_UNAVAILABLE(500, "COMMON_02", "서비스가 일시적으로 사용불가 상태입니다."),

    ROLES_SHOULD_NOT_EMPTY(500, "COMMON_03", "권한은 비어있을 수 없습니다."),
    ;


    private final int status;
    private final String code;
    private final String message;

    ErrorCode(final int status, final String code, final String message) {
        this.status = status;
        this.code = code;
        this.message = message;
    }

    public int getStatus() {
        return status;
    }

    public String getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
