package com.pandaterry.schema_microservice.shared.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {
    // 입력값 관련 에러 (1000번대)
    INVALID_INPUT(400, "SCHEMA_1001", "입력값이 유효하지 않습니다."),
    ORG_ID_NOT_FOUND(400, "SCHEMA_1002", "조직 ID가 존재하지 않습니다."),
    MISSING_HEADER_VALUE(400, "SCHEMA_1003", "헤더 값이 누락되었습니다."),
    
    // 데이터소스 관련 에러 (4000번대)
    DATASOURCE_NOT_FOUND(404, "SCHEMA_4001", "데이터소스를 찾을 수 없습니다."),
    DUPLICATE_DATASOURCE_NAME(409, "SCHEMA_4002", "이미 존재하는 데이터소스 이름입니다."),
    INVALID_DATASOURCE_CONFIG(400, "SCHEMA_4003", "잘못된 데이터소스 설정입니다."),
    DATASOURCE_CONNECTION_FAILED(400, "SCHEMA_4004", "데이터소스 연결에 실패했습니다."),

    // 스키마 관련 에러 (4100번대)
    SCHEMA_NOT_FOUND(404, "SCHEMA_4101", "스키마를 찾을 수 없습니다."),
    DUPLICATE_SCHEMA_NAME(409, "SCHEMA_4102", "이미 존재하는 스키마 이름입니다."),
    INVALID_SCHEMA_NAME(400, "SCHEMA_4103", "잘못된 스키마 이름입니다."),

    // 테이블 관련 에러 (4200번대)
    TABLE_NOT_FOUND(404, "SCHEMA_4201", "테이블을 찾을 수 없습니다."),
    DUPLICATE_TABLE_NAME(409, "SCHEMA_4202", "이미 존재하는 테이블 이름입니다."),
    INVALID_TABLE_NAME(400, "SCHEMA_4203", "잘못된 테이블 이름입니다."),

    // 컬럼 관련 에러 (4300번대)
    COLUMN_NOT_FOUND(404, "SCHEMA_4301", "컬럼을 찾을 수 없습니다."),
    DUPLICATE_COLUMN_NAME(409, "SCHEMA_4302", "이미 존재하는 컬럼 이름입니다."),
    INVALID_COLUMN_NAME(400, "SCHEMA_4303", "잘못된 컬럼 이름입니다."),
    INVALID_DATA_TYPE(400, "SCHEMA_4304", "지원하지 않는 데이터 타입입니다."),

    // 별칭 관련 에러 (4400번대)
    ALIAS_NOT_FOUND(404, "SCHEMA_4401", "별칭을 찾을 수 없습니다."),
    DUPLICATE_ALIAS(409, "SCHEMA_4402", "이미 존재하는 별칭입니다."),
    INVALID_ALIAS(400, "SCHEMA_4403", "잘못된 별칭입니다."),

    // 암호화 관련 에러 (4500번대)
    ENCRYPTION_ERROR(400, "SCHEMA_4501", "암호화 오류가 발생했습니다."),
    DECRYPTION_ERROR(400, "SCHEMA_4502", "복호화 오류가 발생했습니다."),

    // 서버 에러 (5000번대)
    INTERNAL_SERVER_ERROR(500, "SCHEMA_5001", "서버 내부 오류가 발생했습니다.");

    private final int status;
    private final String code;
    private final String message;
}