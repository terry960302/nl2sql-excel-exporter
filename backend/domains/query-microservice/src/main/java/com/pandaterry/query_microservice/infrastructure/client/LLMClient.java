package com.pandaterry.query_microservice.infrastructure.client;

import reactor.core.publisher.Mono;

public interface LLMClient {
    /**
     * LLM에 프롬프트를 전송하고 SQL 쿼리를 받아옵니다.
     *
     * @param prompt LLM에 전송할 프롬프트
     * @return 생성된 SQL 쿼리
     */
    Mono<String> generateSQL(String prompt);
}