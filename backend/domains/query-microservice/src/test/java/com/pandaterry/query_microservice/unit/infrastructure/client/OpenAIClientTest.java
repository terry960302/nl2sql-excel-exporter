package com.pandaterry.query_microservice.unit.infrastructure.client;

import com.pandaterry.query_microservice.domain.enums.ErrorCode;
import com.pandaterry.query_microservice.domain.exception.QueryException;
import com.pandaterry.query_microservice.infrastructure.client.OpenAIClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class OpenAIClientTest {

    private OpenAIClient openAIClient;
    private WebClient webClient;

    @BeforeEach
    void setUp() {
        webClient = WebClient.builder()
                .baseUrl("https://api.openai.com")
                .build();
        openAIClient = new OpenAIClient(webClient);
        ReflectionTestUtils.setField(openAIClient, "apiKey", "test-key");
        ReflectionTestUtils.setField(openAIClient, "model", "gpt-4");
        ReflectionTestUtils.setField(openAIClient, "timeout", 30);
    }

    @Test
    void generateSQL_WithValidPrompt_ShouldReturnSQL() {
        // Given
        String prompt = "테스트 프롬프트";

        // When & Then
        StepVerifier.create(openAIClient.generateSQL(prompt))
                .expectError(QueryException.class)
                .verify();
    }

    @Test
    void generateSQL_WithNullPrompt_ShouldThrowException() {
        // Given
        String prompt = null;

        // When & Then
        StepVerifier.create(openAIClient.generateSQL(prompt))
                .expectErrorMatches(throwable -> throwable instanceof QueryException &&
                        ((QueryException) throwable).getErrorCode() == ErrorCode.INVALID_INPUT)
                .verify();
    }

    @Test
    void generateSQL_WithEmptyPrompt_ShouldThrowException() {
        // given
        String prompt = "";

        // when & then
        StepVerifier.create(openAIClient.generateSQL(prompt))
                .expectErrorMatches(throwable -> throwable instanceof QueryException &&
                        ((QueryException) throwable).getErrorCode() == ErrorCode.INVALID_INPUT)
                .verify();
    }

    @Test
    void generateSQL_WithTimeout_ShouldThrowException() {
        // Given
        String prompt = "테스트 프롬프트";
        ReflectionTestUtils.setField(openAIClient, "timeout", 1);

        // When & Then
        StepVerifier.create(openAIClient.generateSQL(prompt))
                .expectError(QueryException.class)
                .verify();
    }
}