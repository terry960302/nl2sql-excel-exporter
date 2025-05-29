package com.pandaterry.query_microservice.application.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum PromptFragment {
    INTRO("""
            당신은 자연어를 SQL로 변환하는 전문가입니다.
            주어진 스키마 정보를 바탕으로 정확한 SQL 쿼리를 생성해주세요.
            """),

    SCHEMA_DEFINITION("""
            스키마 정보:
            %s
            """),

    NATURAL_LANGUAGE("""
            자연어 요청:
            %s
            """),

    CONSTRAINTS("""
            제약 조건:
            1. SELECT 쿼리만 생성해주세요.
            2. 명시적인 컬럼명을 사용해주세요 (* 사용 금지).
            3. 적절한 JOIN 조건을 포함해주세요.
            4. 성능을 고려한 쿼리를 생성해주세요.
            """),

    EXAMPLE("""
            예시:
            자연어: "이번달에 VIP 고객의 평균 구매 금액을 보여줘"
            SQL: SELECT AVG(purchase_amount) as avg_amount
                 FROM purchases p
                 JOIN customers c ON p.customer_id = c.id
                 WHERE c.grade = 'VIP'
                 AND p.purchase_date >= DATE_TRUNC('month', CURRENT_DATE)
            """);

    private final String template;
}