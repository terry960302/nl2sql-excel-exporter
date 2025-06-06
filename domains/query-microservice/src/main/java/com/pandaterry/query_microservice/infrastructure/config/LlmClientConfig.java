package com.pandaterry.query_microservice.infrastructure.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

import com.pandaterry.query_microservice.domain.enums.ErrorCode;
import com.pandaterry.query_microservice.domain.exception.QueryException;
import com.pandaterry.query_microservice.infrastructure.client.LLMClient;
import com.pandaterry.query_microservice.infrastructure.client.OpenAIClient;
import com.pandaterry.query_microservice.infrastructure.client.PythonServerClient;

@Configuration
class LlmClientConfig {
    @Value("${llm.api.provider}")
    private String provider;

    @Bean
    public LLMClient llmClient(WebClient webClient) {
        return switch (provider.toLowerCase()) {
            case "openai" -> new OpenAIClient(webClient);
            case "python-server" -> new PythonServerClient(webClient);
            default -> throw new QueryException(ErrorCode.LLM_PROVIDER_NOT_SUPPORTED);
        };
    }
}