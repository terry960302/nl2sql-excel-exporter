package com.pandaterry.query_microservice.unit.application.service;

import com.pandaterry.msa_contracts.dto.query.response.SchemaInfoResponse;
import com.pandaterry.query_microservice.application.service.SchemaService;
import com.pandaterry.query_microservice.infrastructure.client.SchemaClient;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("SchemaService 테스트")
class SchemaServiceTest {
        @Mock
        private SchemaClient schemaClient;

        @InjectMocks
        private SchemaService schemaService;

        @Nested
        @DisplayName("getSchemasWithAliases 메서드는")
        class Describe_getSchemasWithAliases {
                @Test
                @DisplayName("스키마 정보와 별칭을 성공적으로 조회한다")
                void getSchemasWithAliases_성공() {
                        // given
                        UUID orgId = UUID.randomUUID();

                        SchemaInfoResponse schemaInfo = SchemaInfoResponse.builder()
                                        .schemaName("test_schema")
                                        .tables(List.of())
                                        .aliases(List.of())
                                        .build();

                        when(schemaClient.getSchemasWithAliases(orgId))
                                        .thenReturn(Mono.just(List.of(schemaInfo)));

                        // when & then
                        StepVerifier.create(schemaService.getSchemasWithAliases(orgId))
                                        .expectNext(List.of(schemaInfo))
                                        .verifyComplete();
                }

                @Test
                @DisplayName("스키마가 없는 경우 빈 리스트를 반환한다")
                void getSchemasWithAliases_스키마없음_성공() {
                        // given
                        UUID orgId = UUID.randomUUID();
                        when(schemaClient.getSchemasWithAliases(orgId))
                                        .thenReturn(Mono.just(List.of()));

                        // when & then
                        StepVerifier.create(schemaService.getSchemasWithAliases(orgId))
                                        .expectNext(List.of())
                                        .verifyComplete();
                }
        }
}