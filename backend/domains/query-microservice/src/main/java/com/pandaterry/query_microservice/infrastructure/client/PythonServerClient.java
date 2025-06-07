package com.pandaterry.query_microservice.infrastructure.client;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class PythonServerClient implements LLMClient {

    private final WebClient webClient;

    @Value("${llm.api.python-server.endpoint}")
    private String endpoint;

    @Value("${llm.api.python-server.model}")
    private String model;

    @Value("${llm.api.timeout}")
    private int timeout;

    @Override
    public Mono<String> generateSQL(String prompt) {
        throw new UnsupportedOperationException("Python server client is not implemented yet");
    } 
}