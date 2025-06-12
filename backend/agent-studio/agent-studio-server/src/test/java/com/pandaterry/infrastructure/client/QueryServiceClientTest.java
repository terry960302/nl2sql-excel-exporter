import com.pandaterry.msa_contracts.constants.HeaderKeys;
import com.pandaterry.infrastructure.client.QueryServiceClient;
import com.pandaterry.msa_contracts.enums.query.JobStatus;
import com.pandaterry.msa_contracts.dto.query.request.JobResultRequest;
import com.pandaterry.presentation.dto.request.QueryRequest;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.MutableHttpRequest;
import io.micronaut.http.client.BlockingHttpClient;
import io.micronaut.http.client.HttpClient;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class QueryServiceClientTest {

    @Test
    void requestQuery_호출경로검증() {
        HttpClient httpClient = mock(HttpClient.class);
        BlockingHttpClient blocking = mock(BlockingHttpClient.class);
        when(httpClient.toBlocking()).thenReturn(blocking);
        when(blocking.retrieve(any(MutableHttpRequest.class), any(Class.class))).thenReturn(null);
        when(blocking.exchange(any(MutableHttpRequest.class))).thenReturn(null);

        QueryServiceClient client = new QueryServiceClient(httpClient, "http://localhost", "secret");
        UUID orgId = UUID.randomUUID();

        client.requestQuery(orgId, new QueryRequest("q"));

        ArgumentCaptor<MutableHttpRequest> captor = ArgumentCaptor.forClass(MutableHttpRequest.class);
        verify(blocking).retrieve(captor.capture(), any(Class.class));
        MutableHttpRequest req = captor.getValue();
        assertThat(req.getUri().getPath()).isEqualTo("/api/v1/queries");
        assertThat(req.getHeaders().get(HeaderKeys.ORG_ID)).isEqualTo(orgId.toString());
    }

    @Test
    void pollAndReport_호출경로검증() {
        HttpClient httpClient = mock(HttpClient.class);
        BlockingHttpClient blocking = mock(BlockingHttpClient.class);
        when(httpClient.toBlocking()).thenReturn(blocking);
        when(blocking.retrieve(any(MutableHttpRequest.class), any(Class.class))).thenReturn(null);

        QueryServiceClient client = new QueryServiceClient(httpClient, "http://localhost", "secret");
        UUID agentId = UUID.randomUUID();
        UUID jobId = UUID.randomUUID();

        client.pollPendingJob(agentId);
        client.reportJobResult(jobId, new JobResultRequest(jobId, JobStatus.COMPLETED, "url", null));

        ArgumentCaptor<MutableHttpRequest> captor = ArgumentCaptor.forClass(MutableHttpRequest.class);
        verify(blocking).retrieve(captor.capture(), any(Class.class));
        MutableHttpRequest req1 = captor.getValue();
        assertThat(req1.getUri().getPath()).isEqualTo("/api/v1/jobs");
        assertThat(req1.getUri().getQuery()).isEqualTo("agentId=" + agentId + "&status=PENDING");

        ArgumentCaptor<MutableHttpRequest> captor2 = ArgumentCaptor.forClass(MutableHttpRequest.class);
        verify(blocking).exchange(captor2.capture());
        MutableHttpRequest req2 = captor2.getValue();
        assertThat(req2.getUri().getPath()).isEqualTo("/api/v1/jobs/" + jobId + "/result");
    }
}
