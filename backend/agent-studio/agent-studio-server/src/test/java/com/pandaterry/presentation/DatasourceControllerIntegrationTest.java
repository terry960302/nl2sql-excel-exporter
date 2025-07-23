package com.pandaterry.presentation;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pandaterry.msa_contracts.constants.RoutePath;
import com.pandaterry.msa_contracts.dto.schema.request.RegisterSchemaRequest;
import com.pandaterry.msa_contracts.dto.schema.response.DatasourceResponse;
import com.pandaterry.msa_contracts.dto.schema.response.RegisterSchemaResponse;
import com.pandaterry.msa_contracts.enums.schema.DatabaseEngineType;
import com.pandaterry.msa_contracts.enums.schema.DatabaseType;
import com.pandaterry.msa_contracts.vo.schema.TableSchema;
import com.pandaterry.presentation.dto.request.DatabaseConnectionRequest;
import com.pandaterry.presentation.dto.request.ScanSchemaRequest;
import com.pandaterry.presentation.dto.response.DatabaseConnectionResponse;
import com.pandaterry.presentation.dto.response.ScanSchemaResponse;
import io.micronaut.core.type.Argument;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.MutableHttpRequest;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.reactor.http.client.ReactorHttpClient;
import io.micronaut.scheduling.TaskExecutors;
import io.micronaut.scheduling.annotation.ExecuteOn;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import org.junit.Ignore;
import org.junit.jupiter.api.*;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.util.List;
import java.util.UUID;

@MicronautTest(environments = "test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class) // 순서 기반 실행
@Testcontainers
@Tag("Docker")
public class DatasourceControllerIntegrationTest {

    @Inject
    @Client(RoutePath.Datasource.BASE)
    private ReactorHttpClient datasourceClient;

    @Inject
    @Client(RoutePath.Schema.BASE)
    private ReactorHttpClient schemaClient;

    private static final String ACCESS_TOKEN = "eyJhbGciOiJIUzUxMiJ9.eyJYLVVzZXItSWQiOiI5ZDEzOTY0NS00ZjgxLTQ1N2YtODA0My0xNTJjYzY0ZjhjOTMiLCJwbGFuTmFtZSI6IkJBU0lDIiwiWC1Pcmdhbml6YXRpb24tSWQiOiI3YWU5NmFkMy1mNTg2LTQ1NDQtYjU5Ny1iOWE3NDlmYzc1N2UiLCJYLVJvbGVzIjpbIlVTRVIiXSwiZW1haWwiOiJ0ZXJyeTk2MDMwMkBnbWFpbC5jb20iLCJzdWIiOiI5ZDEzOTY0NS00ZjgxLTQ1N2YtODA0My0xNTJjYzY0ZjhjOTMiLCJpYXQiOjE3NDk3MDc0NTEsImV4cCI6MTc1MjI5OTQ1MSwianRpIjoiYTQ5NmQyNGQtNTM0ZC00Njk4LThlMGEtZWYyODRhNDliNTQyIn0.gE3MNOJrI5k_8WfSniMLVyJBe46OsJi8IqYZ38geWxl1au3Qss6vGwSSFxjPfOSEVQte5Fos_neFiZsWp0Aemw";


    private static UUID datasourceId;
    private static List<TableSchema> schemas;
    private static String rawSchema;

    @Inject
    @Named("jacksonMapper")
    private ObjectMapper objectMapper;
    private static final String DB_NAME = "test";
    private static final String DB_USERNAME = "postgres";
    private static final String DB_PW = "postgres";
    private static final int DB_PORT = 5432;
    private static final PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>(DockerImageName.parse("postgres:15-alpine"));


    @BeforeAll
    public static void setup(){
        postgres.start();
    }

    @AfterAll
    public static void end(){
        postgres.stop();
    }

    @Test
    @Order(1)
    @DisplayName(value = "datasource 목록을 가져올 수 있어야한다.")
    public void testGetDatasources() {
        HttpRequest<?> request = HttpRequest.GET("/")
                .bearerAuth(ACCESS_TOKEN);

        datasourceClient
                .exchange(request, Argument.listOf(DatasourceResponse.class))
                .subscribe(response -> {
                    Assertions.assertTrue(response.body().size() >= 0);
                });

    }

    @Test
    @Order(2)
    @DisplayName(value = "Postgresql에 대한 실제 연결 테스트 - datasourceId가 null이 아니어야한다.")
    @Ignore
    public void testConnection() {

        DatabaseConnectionRequest requestDtos = new DatabaseConnectionRequest(null,
                postgres.getDatabaseName(),
                "테스트 디비",
                "테스트 디비입니다.",
                postgres.getHost(), // 실제로 구동되고 잇는 디비여야함.
                DB_PORT,
                postgres.getUsername(),
                postgres.getPassword(),
                DatabaseType.RDB,
                DatabaseEngineType.POSTGRESQL);

        MutableHttpRequest<DatabaseConnectionRequest> request = HttpRequest.POST(RoutePath.Datasource.TEST_SUFFIX, requestDtos)
                .bearerAuth(ACCESS_TOKEN);
        datasourceClient.exchange(request, DatabaseConnectionResponse.class)
                .subscribe(response -> {
                    datasourceId = response.body().getDatasourceId();
                    Assertions.assertNotNull(response.body().getDatasourceId());
                });

    }

    @Test
    @Order(3)
    @DisplayName(value = "연결된 데이터소스의 스키마를 읽어들일 수 있어야한다.")
    public void testScanSchema() {
        ScanSchemaRequest dto = ScanSchemaRequest.builder()
                .datasourceId(datasourceId)
                .engineType(DatabaseEngineType.POSTGRESQL)
                .build();

        MutableHttpRequest<ScanSchemaRequest> request = HttpRequest.POST(RoutePath.Schema.SCAN_SUFFIX, dto)
                .bearerAuth(ACCESS_TOKEN);


        schemaClient.exchange(request, ScanSchemaResponse.class)
                .subscribe(response -> {
                    schemas = response.body().getSchemas();
                    rawSchema = response.body().getRawSchema();

                    Assertions.assertEquals(response.body().getDatasourceId(), datasourceId);
                    Assertions.assertTrue(response.body().getSchemas().size() > 0);
                    Assertions.assertTrue(response.body().getRawSchema().length() > 0);
                });

    }

    @Test
    @Order(4)
    @DisplayName("스캔한 스키마를 MSA로 업로드할 수 있어야한다.")
    public void testUploadSchema() {
        RegisterSchemaRequest dto = RegisterSchemaRequest.builder()
                .datasourceId(datasourceId)
                .schemas(schemas)
                .rawSchema(rawSchema)
                .build();

        MutableHttpRequest<RegisterSchemaRequest> request = HttpRequest.POST(RoutePath.Schema.REGISTER_SUFFIX, dto)
                .bearerAuth(ACCESS_TOKEN);

        schemaClient.exchange(request, RegisterSchemaResponse.class)
                .subscribe(response -> {
                    Assertions.assertEquals(datasourceId, response.body().getDatasourceId());
                });
    }

//    private <T> String objectToString(T dto) {
//        try {
//            return objectMapper.writeValueAsString(dto);
//        } catch (JsonProcessingException e) {
//            throw new RuntimeException("직렬화하는데 실패했습니다 -> " + e);
//        }
//    }
//
//    private <T> T stringToObject(String json, Class<T> type) {
//        try {
//            return objectMapper.readValue(json, type);
//        } catch (JsonProcessingException e) {
//            throw new RuntimeException("직렬화하는데 실패했습니다 -> " + e);
//        }
//    }
//
//    private <T> List<T> stringToList(String json, Class<T> C) {
//        try {
//            return objectMapper.readValue(json, new TypeReference<List<T>>() {
//            });
//        } catch (JsonProcessingException e) {
//            throw new RuntimeException("직렬화하는데 실패했습니다 -> " + e);
//        }
//    }
}
