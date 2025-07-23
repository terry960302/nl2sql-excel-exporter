package com.pandaterry.presentation;

import com.pandaterry.msa_contracts.constants.RoutePath;
import com.pandaterry.msa_contracts.dto.quota.request.QuotaUsageRecordRequest;
import com.pandaterry.msa_contracts.dto.quota.response.QuotaOrgResponse;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.MutableHttpRequest;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.reactor.http.client.ReactorHttpClient;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.*;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.kafka.KafkaContainer;
import org.testcontainers.utility.DockerImageName;

@MicronautTest(environments = "test")
public class QuotaControllerIntegrationTest {
    private static final String ACCESS_TOKEN = "eyJhbGciOiJIUzUxMiJ9.eyJYLVVzZXItSWQiOiI5ZDEzOTY0NS00ZjgxLTQ1N2YtODA0My0xNTJjYzY0ZjhjOTMiLCJwbGFuTmFtZSI6IkJBU0lDIiwiWC1Pcmdhbml6YXRpb24tSWQiOiI3YWU5NmFkMy1mNTg2LTQ1NDQtYjU5Ny1iOWE3NDlmYzc1N2UiLCJYLVJvbGVzIjpbIlVTRVIiXSwiZW1haWwiOiJ0ZXJyeTk2MDMwMkBnbWFpbC5jb20iLCJzdWIiOiI5ZDEzOTY0NS00ZjgxLTQ1N2YtODA0My0xNTJjYzY0ZjhjOTMiLCJpYXQiOjE3NDk3MDc0NTEsImV4cCI6MTc1MjI5OTQ1MSwianRpIjoiYTQ5NmQyNGQtNTM0ZC00Njk4LThlMGEtZWYyODRhNDliNTQyIn0.gE3MNOJrI5k_8WfSniMLVyJBe46OsJi8IqYZ38geWxl1au3Qss6vGwSSFxjPfOSEVQte5Fos_neFiZsWp0Aemw";

    @Inject
    @Client(RoutePath.Quota.BASE)
    private ReactorHttpClient client;

    @Test
    @DisplayName("내가 속한 조직의 사용량을 조회할 수 있어야한다.")
    public void testGetMyOrgQuota() {
        MutableHttpRequest<?> request = HttpRequest.GET(RoutePath.Quota.ORG_ME_SUFFIX)
                .bearerAuth(ACCESS_TOKEN);

        client.exchange(request, QuotaOrgResponse.class)
                .subscribe(response -> {
                    QuotaOrgResponse body = response.body();
                    Assertions.assertTrue(body.getMonthlyQuota() >= 0);
                    Assertions.assertTrue(body.getRemainingQuota() >= 0);
                    Assertions.assertTrue(body.getTodayCount() >= 0);
                    Assertions.assertTrue(body.getMonthCount() >= 0);
                });
    }

    @Test
    @DisplayName("(강제기록용-원래는 쿼리서비스에서 카프카로 기록) 엑셀 생성 성공 record를 업로드할 수 있어야한다.")
    public void testRecordQuota() {
        QuotaUsageRecordRequest dto = QuotaUsageRecordRequest.builder()
                .increment(1)
                .build();

        Assertions.assertDoesNotThrow(() -> {
            MutableHttpRequest<QuotaUsageRecordRequest> request = HttpRequest.POST(RoutePath.Quota.USAGE_SUFFIX, dto)
                    .bearerAuth(ACCESS_TOKEN);
            client.exchange(request);
        });
    }
}
