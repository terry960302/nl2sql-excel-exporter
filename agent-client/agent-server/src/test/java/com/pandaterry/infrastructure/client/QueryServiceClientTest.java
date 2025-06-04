package com.pandaterry.infrastructure.client;

import com.pandaterry.domain.model.ExecutionJob;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.client.BlockingHttpClient;
import io.micronaut.http.client.HttpClient;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;

import java.lang.reflect.Field;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class QueryServiceClientTest {

    @Mock
    HttpClient httpClient;

    @Mock
    BlockingHttpClient blockingClient;

    @Test
    @DisplayName("pollPendingJob 예외 발생 시 로그를 남기고 Optional.empty()를 반환해야 한다")
    void pollPendingJob_logsErrorAndReturnsEmpty() throws Exception {
        when(httpClient.toBlocking()).thenReturn(blockingClient);
        RuntimeException ex = new RuntimeException("fail");
        when(blockingClient.retrieve(any(HttpRequest.class), eq(ExecutionJob.class))).thenThrow(ex);

        QueryServiceClient client = new QueryServiceClient(httpClient, "http://localhost", "secret");

        Field field = QueryServiceClient.class.getDeclaredField("logger");
        field.setAccessible(true);
        Logger original = (Logger) field.get(null);
        Logger mockLogger = mock(Logger.class);
        field.set(null, mockLogger);
        try {
            Optional<ExecutionJob> result = client.pollPendingJob(UUID.randomUUID());
            assertThat(result).isEmpty();
            verify(mockLogger).error("Failed to poll job", ex);
        } finally {
            field.set(null, original);
        }
    }
}
