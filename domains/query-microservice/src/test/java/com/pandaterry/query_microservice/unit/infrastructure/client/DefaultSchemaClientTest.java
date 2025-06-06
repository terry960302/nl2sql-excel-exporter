package com.pandaterry.query_microservice.unit.infrastructure.client;

import com.pandaterry.msa_contracts.dto.query.response.ColumnInfoResponse;
import com.pandaterry.msa_contracts.dto.query.response.SchemaInfoResponse;
import com.pandaterry.msa_contracts.dto.query.response.TableInfoResponse;
import com.pandaterry.query_microservice.application.vo.ColumnInfo;
import com.pandaterry.query_microservice.application.vo.DataSourceInfo;
import com.pandaterry.query_microservice.domain.enums.ErrorCode;
import com.pandaterry.query_microservice.domain.exception.QueryException;
import com.pandaterry.query_microservice.infrastructure.client.SchemaClient;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DefaultSchemaClientTest {

    @Mock
    private WebClient webClient;

    @Mock
    @SuppressWarnings("rawtypes")
    private WebClient.RequestHeadersUriSpec requestHeadersUriSpec;

    @Mock
    @SuppressWarnings("rawtypes")
    private WebClient.RequestHeadersSpec requestHeadersSpec;

    @Mock
    private WebClient.ResponseSpec responseSpec;

    private SchemaClient schemaClient;

    @BeforeEach
    void setUp() {
        schemaClient = new SchemaClient(webClient);
        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(any(String.class))).thenReturn(requestHeadersSpec);
        lenient().when(requestHeadersSpec.header(eq("X-Organization-Id"), any()))
                .thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
    }

    @Nested
    @DisplayName("getSchemasForOrg 메서드는")
    class Describe_getSchemasForOrg {

        @Test
        @DisplayName("유효한 조직 ID로 호출 시 스키마 목록을 반환한다")
        void getSchemasForOrg_WithValidOrgId_ShouldReturnSchemas() {
            // given
            UUID orgId = UUID.randomUUID();
            List<SchemaInfoResponse> expectedSchemas = List.of(
                    SchemaInfoResponse.builder()
                            .schemaName("schema1")
                            .tables(List.of(
                                    TableInfoResponse.builder()
                                            .tableName("table1")
                                            .columns(List.of(
                                                    ColumnInfoResponse.builder()
                                                            .columnName("column1")
                                                            .dataType("varchar")
                                                            .build(),
                                                    ColumnInfoResponse.builder()
                                                            .columnName("column2")
                                                            .dataType("int")
                                                            .build()))
                                            .build()))
                            .aliases(List.of())
                            .build());

            when(responseSpec.bodyToMono(new ParameterizedTypeReference<List<SchemaInfoResponse>>() {
            }))
                    .thenReturn(Mono.just(expectedSchemas));

            // when
            Mono<List<SchemaInfoResponse>> result = schemaClient.getSchemasForOrg(orgId);

            // then
            StepVerifier.create(result)
                    .expectNext(expectedSchemas)
                    .verifyComplete();
        }

        @Test
        @DisplayName("API 호출 실패 시 QueryException을 발생시킨다")
        void getSchemasForOrg_WhenErrorOccurs_ShouldThrowQueryException() {
            // given
            UUID orgId = UUID.randomUUID();
            when(responseSpec.bodyToMono(new ParameterizedTypeReference<List<SchemaInfoResponse>>() {
            }))
                    .thenReturn(Mono.error(new RuntimeException("API Error")));

            // when
            Mono<List<SchemaInfoResponse>> result = schemaClient.getSchemasForOrg(orgId);

            // then
            StepVerifier.create(result)
                    .expectError(QueryException.class)
                    .verify();
        }
    }

    @Nested
    @DisplayName("getDataSourceInfo 메서드는")
    class Describe_getDataSourceInfo {

        @Test
        @DisplayName("유효한 데이터소스 ID로 호출 시 데이터소스 정보를 반환한다")
        void getDataSourceInfo_WithValidDatasourceId_ShouldReturnDataSourceInfo() {
            // given
            UUID datasourceId = UUID.randomUUID();
            DataSourceInfo expectedInfo = DataSourceInfo.builder()
                    .id(datasourceId)
                    .name("test-db")
                    .host("localhost")
                    .port(5432)
                    .database("test")
                    .username("username")
                    .encryptedPassword("password")
                    .type("postgresql")
                    .build();

            when(responseSpec.bodyToMono(DataSourceInfo.class))
                    .thenReturn(Mono.just(expectedInfo));

            // when
            Mono<DataSourceInfo> result = schemaClient.getDataSourceInfo(datasourceId);

            // then
            StepVerifier.create(result)
                    .expectNext(expectedInfo)
                    .verifyComplete();
        }

        @Test
        @DisplayName("API 호출 실패 시 QueryException을 발생시킨다")
        void getDataSourceInfo_WhenErrorOccurs_ShouldThrowQueryException() {
            // given
            UUID datasourceId = UUID.randomUUID();
            when(responseSpec.bodyToMono(DataSourceInfo.class))
                    .thenReturn(Mono.error(new QueryException(ErrorCode.INTERNAL_SERVER_ERROR)));

            // when
            Mono<DataSourceInfo> result = schemaClient.getDataSourceInfo(datasourceId);

            // then
            StepVerifier.create(result)
                    .expectError(QueryException.class)
                    .verify();
        }
    }
}