package com.pandaterry.query_microservice.domain.validator;

import com.pandaterry.query_microservice.domain.exception.QueryException;
import reactor.core.publisher.Mono;

public interface SqlValidator {
    /**
     * SQL 쿼리를 검증합니다.
     *
     * @param sql 검증할 SQL 쿼리
     * @return 검증된 SQL 쿼리
     * @throws QueryException 검증 실패 시 발생
     */
    Mono<String> validate(String sql);
}