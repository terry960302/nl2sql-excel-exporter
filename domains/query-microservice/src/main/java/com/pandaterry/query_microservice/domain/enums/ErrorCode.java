package com.pandaterry.query_microservice.domain.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {
    // 공통 에러 (C001 ~ C099)
    INVALID_INPUT(400, "C001", "잘못된 입력값입니다."),
    INTERNAL_SERVER_ERROR(500, "C002", "서버 내부 오류가 발생했습니다."),

    // 자연어 쿼리 검증 에러 (N001 ~ N099)
    EMPTY_QUERY(400, "N001", "자연어 질의는 비어있을 수 없습니다."),
    QUERY_TOO_LONG(400, "N002", "자연어 질의는 200자를 초과할 수 없습니다."),
    FORBIDDEN_KEYWORD(400, "N003", "허용되지 않은 SQL 키워드가 포함되어 있습니다."),
    INVALID_CHARACTER(400, "N004", "유효하지 않은 문자가 포함되어 있습니다."),

    // 프롬프트 관련 에러 (P001 ~ P099)
    PROMPT_ASSEMBLY_FAILURE(500, "P001", "프롬프트 구성에 실패했습니다."),
    PROMPT_CONTEXT_INCOMPLETE(400, "P002", "프롬프트 컨텍스트가 불완전합니다."),
    PROMPT_TEMPLATE_NOT_FOUND(404, "P003", "프롬프트 템플릿을 찾을 수 없습니다."),
    PROMPT_GENERATION_TIMEOUT(408, "P004", "프롬프트 생성 시간이 초과되었습니다."),
    PROMPT_ENCODING_ERROR(400, "P005", "프롬프트에 허용되지 않은 문자가 포함되어 있습니다."),

    // LLM 관련 에러 (L001 ~ L099)
    LLM_SYSTEM_ERROR(500, "L001", "LLM 시스템 오류가 발생했습니다."),
    LLM_SCHEMA_MISMATCH(400, "L002", "요청한 컬럼/테이블이 현재 스키마에 존재하지 않습니다."),
    LLM_AMBIGUOUS_INPUT(400, "L003", "자연어 표현이 모호하거나 여러 의미로 해석될 수 있습니다."),
    LLM_STREAM_TIMEOUT(408, "L004", "LLM 응답 시간이 초과되었습니다."),
    LLM_PROVIDER_NOT_SUPPORTED(400, "L005", "지원되지 않는 LLM 제공자입니다."),

    // SQL 검증 관련 에러 (S001 ~ S099)
    SQL_VALIDATION_FAILED(400, "S001", "SQL 검증에 실패했습니다."),
    SQL_NOT_SELECT_ONLY(400, "S002", "SELECT 쿼리만 허용됩니다."),
    SQL_INCOMPLETE(400, "S003", "SQL이 완성되지 않았습니다."),
    SQL_TOO_COMPLEX(400, "S004", "SQL이 너무 복잡합니다."),
    SQL_JOIN_CONDITION_MISSING(400, "S005", "조인 조건이 누락되었습니다."),
    SQL_COLUMN_ALIAS_MISSING(400, "S006", "컬럼 별칭이 누락되었습니다."),
    SQL_WILDCARD_NOT_ALLOWED(400, "S007", "와일드카드(*)는 허용되지 않습니다."),
    SQL_SUBQUERY_DEPTH_EXCEEDED(400, "S008", "서브쿼리 깊이가 제한을 초과했습니다."),

    // 쿼리 실행 관련 에러 (Q001 ~ Q099)
    QUERY_RESULT_SIZE_EXCEEDED(400, "Q001", "쿼리 결과 크기가 제한을 초과했습니다."),
    QUERY_COUNT_ESTIMATION_FAILED(500, "Q002", "결과 행 수 예측에 실패했습니다."),
    QUERY_ROW_SIZE_TOO_LARGE(400, "Q003", "개별 행의 크기가 너무 큽니다."),
    QUERY_EXECUTION_FAILED(500, "Q004", "쿼리 실행에 실패했습니다."),
    QUERY_TIMEOUT(408, "Q005", "쿼리 실행 시간이 초과되었습니다.");

    private final int status;
    private final String code;
    private final String message;
}