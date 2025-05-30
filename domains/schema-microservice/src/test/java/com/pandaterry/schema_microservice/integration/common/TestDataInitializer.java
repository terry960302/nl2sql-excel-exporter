package com.pandaterry.schema_microservice.integration.common;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class TestDataInitializer {

    @Transactional
    public void initialize() {
        // 테스트 데이터베이스 초기화 로직
    }

    @Transactional
    public void cleanup() {
        // 테스트 데이터 정리 로직
    }
}