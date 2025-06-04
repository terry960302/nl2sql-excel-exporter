package com.pandaterry.query_microservice.application.service;

import com.pandaterry.query_microservice.application.dto.*;
import com.pandaterry.query_microservice.application.dto.request.NaturalLanguageQueryRequest;
import com.pandaterry.query_microservice.application.vo.PromptContext;
import com.pandaterry.query_microservice.application.vo.SchemaInfo;
import com.pandaterry.query_microservice.infrastructure.client.LLMClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class QueryService {
        private final LLMClient llmClient;
        private final SchemaService schemaService;
        private final PromptService promptService;

        public Mono<String> convertToSQL(NaturalLanguageQueryRequest request) {
                return schemaService.getSchemasWithAliases(request.getOrgId())
                                .flatMap(schemaResponses -> {
                                        List<SchemaInfo> schemas = schemaResponses.stream()
                                                        .map(SchemaInfo::from)
                                                        .collect(Collectors.toList());

                                        PromptContext context = PromptContext.builder()
                                                        .schemas(schemas)
                                                        .naturalText(request.getNaturalText())
                                                        .orgId(request.getOrgId())
                                                        .userId(request.getUserId())
                                                        .build();

                                        String prompt = promptService.generatePrompt(context);
                                        return llmClient.generateSQL(prompt);
                                });
        }

}