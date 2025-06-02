package com.pandaterry.domain.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {
    // 내부 에러 (Agent 서버 내부)
    DATABASE_CONNECTION_FAILED("DB_001", "데이터베이스 연결에 실패했습니다"),
    DATABASE_NOT_CONNECTED("DB_002", "데이터베이스가 연결되어 있지 않습니다"),
    DATABASE_SCHEMA_SCAN_FAILED("DB_003", "데이터베이스 스키마 스캔에 실패했습니다"),
    INVALID_DATABASE_TYPE("DB_004", "지원하지 않는 데이터베이스 타입입니다"),
    SCHEMA_PARSING_FAILED("DB_005", "스키마 파싱에 실패했습니다"),

    // 외부 서비스 에러 (SchemaService, QueryService, QuotaService 등)
    QUERY_SERVICE_ERROR("EXT_001", "쿼리 서비스 통신 중 오류가 발생했습니다"),
    QUOTA_SERVICE_ERROR("EXT_002", "할당량 서비스 통신 중 오류가 발생했습니다"),
    SERVICE_CONNECTION_FAILED("EXT_003", "외부 서비스 연결에 실패했습니다"),
    SERVICE_RESPONSE_INVALID("EXT_004", "외부 서비스 응답이 유효하지 않습니다"),

    // 공통 에러
    INTERNAL_SERVER_ERROR("COM_001", "서버 내부 오류가 발생했습니다"),
    INVALID_REQUEST("COM_002", "잘못된 요청입니다"),
    RESOURCE_NOT_FOUND("COM_003", "요청한 리소스를 찾을 수 없습니다");

    private final String code;
    private final String message;
}