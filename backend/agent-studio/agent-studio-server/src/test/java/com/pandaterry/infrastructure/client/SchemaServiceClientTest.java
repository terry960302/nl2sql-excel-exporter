import com.pandaterry.msa_contracts.constants.HeaderKeys;
import com.pandaterry.msa_contracts.dto.schema.request.RegisterSchemaRequest;
import com.pandaterry.msa_contracts.vo.schema.TableSchema;
import com.pandaterry.infrastructure.client.SchemaServiceClient;
import io.micronaut.http.MutableHttpRequest;
import io.micronaut.http.client.BlockingHttpClient;
import io.micronaut.http.client.HttpClient;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class SchemaServiceClientTest {

    @Test
    void uploadSchema_호출경로검증() {
        HttpClient httpClient = mock(HttpClient.class);
        BlockingHttpClient blocking = mock(BlockingHttpClient.class);
        when(httpClient.toBlocking()).thenReturn(blocking);
        when(blocking.exchange(any(MutableHttpRequest.class))).thenReturn(null);

        SchemaServiceClient client = new SchemaServiceClient(httpClient, "http://localhost", "secret");

        UUID orgId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        UUID agentId = UUID.randomUUID();
        UUID dsId = UUID.randomUUID();

        RegisterSchemaRequest req = new RegisterSchemaRequest(orgId, userId, agentId, dsId, "ds", List.of(), "raw");
        client.uploadSchema(req);

        ArgumentCaptor<MutableHttpRequest> captor = ArgumentCaptor.forClass(MutableHttpRequest.class);
        verify(blocking).exchange(captor.capture());
        MutableHttpRequest r = captor.getValue();
        assertThat(r.getUri().getPath()).isEqualTo("/api/v1/schemas");
        assertThat(r.getHeaders().get(HeaderKeys.ORG_ID)).isEqualTo(orgId.toString());
        assertThat(r.getHeaders().get(HeaderKeys.USER_ID)).isEqualTo(userId.toString());
        assertThat(r.getHeaders().get(HeaderKeys.AGENT_ID)).isEqualTo(agentId.toString());
    }
}
