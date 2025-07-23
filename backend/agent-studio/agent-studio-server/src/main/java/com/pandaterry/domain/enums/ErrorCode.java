package com.pandaterry.domain.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {
    // 내부 에러 (Agent 서버 내부)
    DATASOURCE_NOT_REGISTERED(500, "DB_000", "데이터소스 등록에 실패했습니다."),
    DATABASE_CONNECTION_FAILED(500, "DB_001", "데이터베이스 연결에 실패했습니다"),
    DATABASE_NOT_CONNECTED(500, "DB_002", "데이터베이스가 연결되어 있지 않습니다"),
    DATABASE_SCHEMA_SCAN_FAILED(500, "DB_003", "데이터베이스 스키마 스캔에 실패했습니다"),
    INVALID_DATABASE_TYPE(400, "DB_004", "지원하지 않는 데이터베이스 타입입니다"),
    SCHEMA_PARSING_FAILED(500, "DB_005", "스키마 파싱에 실패했습니다"),
    DATASOURCE_NOT_CLOSED_BEFORE_REMOVE(500, "DB_007", "커넥션풀을 삭제하려면 그 전에 커넥션 풀을 해제해야 합니다."),

    FILE_AWS_PRESIGNED_DOWNLOAD_FAILED(500, "FILE_001", "Aws Presigned URL 파일 다운로드에 실패했습니다."),
    FILE_TEMP_WRITE_FAILED(500, "FILE_002", "임시 파일을 생성하는데 실패했습니다."),

    ROWS_EMPTRY(500, "EXCEL_001", "SQL로 반환된 행이 비어있습니다."),

    // 외부 서비스 에러 (SchemaService, QueryService, QuotaService 등)
    QUERY_SERVICE_ERROR(500, "EXT_001", "쿼리 서비스 통신 중 오류가 발생했습니다"),
    QUOTA_SERVICE_ERROR(500, "EXT_002", "할당량 서비스 통신 중 오류가 발생했습니다"),
    SERVICE_CONNECTION_FAILED(500, "EXT_003", "외부 서비스 연결에 실패했습니다"),
    SERVICE_RESPONSE_INVALID(500, "EXT_004", "외부 서비스 응답이 유효하지 않습니다"),

    // 공통 에러
    INTERNAL_SERVER_ERROR(500, "COM_001", "서버 내부 오류가 발생했습니다"),
    INVALID_REQUEST(400, "COM_002", "잘못된 요청입니다"),
    RESOURCE_NOT_FOUND(404, "COM_003", "요청한 리소스를 찾을 수 없습니다"),

    ;

    private final int status;
    private final String code;
    private final String message;
}