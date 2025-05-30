package com.pandaterry.query_microservice.infrastructure.client;

import com.pandaterry.query_microservice.domain.enums.ErrorCode;
import com.pandaterry.query_microservice.domain.exception.QueryException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.Duration;

@Component
@RequiredArgsConstructor
public class OpenAIClient implements LLMClient {

    private final WebClient webClient;

    @Value("${llm.api.openai.key}")
    private String apiKey;

    @Value("${llm.api.openai.model}")
    private String model;

    @Value("${llm.api.timeout}")
    private int timeout;

    @Override
    public Mono<String> generateSQL(String prompt) {
        if (prompt == null || prompt.isBlank()) {
            return Mono.error(new QueryException(ErrorCode.INVALID_INPUT));
        }

        return webClient.post()
                .uri("/v1/chat/completions")
                .header("Authorization", "Bearer " + apiKey)
                .bodyValue(new ChatCompletionRequest(model, prompt))
                .retrieve()
                .bodyToMono(ChatCompletionResponse.class)
                .timeout(Duration.ofSeconds(timeout))
                .map(response -> {
                    if (response.choices() == null || response.choices().isEmpty()) {
                        throw new QueryException(ErrorCode.LLM_SYSTEM_ERROR);
                    }
                    return response.choices().get(0).message().content();
                })
                .onErrorMap(e -> {
                    if (e instanceof java.util.concurrent.TimeoutException) {
                        return new QueryException(ErrorCode.LLM_STREAM_TIMEOUT);
                    }
                    if (e instanceof org.springframework.web.reactive.function.client.WebClientResponseException.BadRequest) {
                        return new QueryException(ErrorCode.LLM_AMBIGUOUS_INPUT);
                    }
                    return new QueryException(ErrorCode.LLM_SYSTEM_ERROR);
                });
    }

    private record ChatCompletionRequest(
            String model,
            String prompt) {
        public ChatCompletionRequest {
            if (model == null || model.isBlank()) {
                throw new QueryException(ErrorCode.INVALID_INPUT);
            }
            if (prompt == null || prompt.isBlank()) {
                throw new QueryException(ErrorCode.INVALID_INPUT);
            }
        }
    }

    private record ChatCompletionResponse(
            String id,
            String object,
            long created,
            String model,
            java.util.List<Choice> choices) {
        private record Choice(
                Message message,
                String finish_reason,
                int index) {
            private record Message(
                    String role,
                    String content) {
            }
        }
    }
}