package com.pandaterry.query_microservice.domain.validator;

import reactor.core.publisher.Mono;
import java.util.UUID;

public interface QuerySizeValidator {
    /**
     * SQL 쿼리의 예상 결과 크기를 검증합니다.
     *
     * @param sql          검증할 SQL 쿼리
     * @param datasourceId 데이터소스 ID
     * @return 검증된 SQL 쿼리
     */
    Mono<String> validate(String sql, UUID datasourceId);
}