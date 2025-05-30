package com.pandaterry.auth_microservice.domain.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {
    // 인증 관련 에러 (4000번대)
    DUPLICATE_EMAIL(409, "AUTH_4001", "이미 등록된 이메일입니다."),
    INVALID_CREDENTIALS(401, "AUTH_4002", "이메일 또는 비밀번호가 잘못되었습니다."),
    USER_NOT_FOUND(404, "AUTH_4003", "사용자를 찾을 수 없습니다."),
    INVALID_TOKEN(401, "AUTH_4004", "유효하지 않은 토큰입니다."),
    WEAK_PASSWORD(400, "AUTH_4005", "비밀번호는 8자 이상이며, 대문자, 소문자, 숫자, 특수문자를 포함해야 합니다."),
    TOKEN_EXPIRED(401, "AUTH_4006", "만료된 토큰입니다."),
    TOKEN_REVOKED(401, "AUTH_4007", "이미 사용된 토큰입니다."),
    DISPLAY_NAME_EMPTY(400, "AUTH_4008", "표시 이름은 비어있을 수 없습니다."),
    PLAN_NOT_FOUND(404, "AUTH_4009", "플랜을 찾을 수 없습니다."),

    // 서버 에러 (5000번대)
    INTERNAL_SERVER_ERROR(500, "AUTH_5001", "서버 내부 오류가 발생했습니다.");

    private final int status;
    private final String code;
    private final String message;
}