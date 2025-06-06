package com.pandaterry.application.service.query;

import com.pandaterry.application.facade.QueryJobProcessFacade;
import com.pandaterry.application.vo.ExcelResult;
import com.pandaterry.msa_contracts.dto.query.request.JobResultRequest;
import com.pandaterry.msa_contracts.dto.query.request.NaturalLanguageQueryRequest;
import com.pandaterry.msa_contracts.dto.query.response.NaturalLanguageQueryResponse;
import com.pandaterry.presentation.dto.request.QueryRequest;
import com.pandaterry.msa_contracts.enums.query.JobStatus;
import com.pandaterry.domain.model.ExecutionJob;
import com.pandaterry.infrastructure.client.QueryServiceClient;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class QueryJobProcessorTest {

    @Mock
    private QueryServiceClient queryServiceClient;
    @Mock
    private JobPollingService jobPollingService;
    @Mock
    private JobExecutionService jobExecutionService;

    @InjectMocks
    private QueryJobProcessFacade processor;

    @Test
    @DisplayName("정상적인 Job 처리 시 결과 파일 경로가 반환되어야 한다")
    void requestAndProcess_success() throws Exception {
        UUID orgId = UUID.randomUUID();
        UUID agentId = UUID.randomUUID();
        UUID datasourceId = UUID.randomUUID();
        UUID jobId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        ExecutionJob job = new ExecutionJob(jobId.toString(), "PENDING", "select 1", LocalDateTime.now(), LocalDateTime.now());

        when(queryServiceClient.requestQuery(eq(orgId), any(QueryRequest.class))).thenReturn(Optional.of(job));
        when(jobPollingService.poll(agentId, jobId)).thenReturn(job);
        Path resultPath = Path.of("result.xlsx");
        String filename = resultPath.getFileName().toString();
        String path = resultPath.toString();
        when(jobExecutionService.execute(datasourceId, job.query(), jobId)).thenReturn(new ExcelResult(filename, path));

        NaturalLanguageQueryResponse result = processor.handleQuery(
                NaturalLanguageQueryRequest.builder()
                        .naturalText("select 1")
                        .agentId(agentId)
                        .orgId(orgId)
                        .userId(userId)
                        .build()
        );
        assertThat(result.downloadUrl()).contains(path);
        ArgumentCaptor<JobResultRequest> captor = ArgumentCaptor.forClass(JobResultRequest.class);
        verify(queryServiceClient).reportJobResult(eq(jobId), captor.capture());
        JobResultRequest reported = captor.getValue();
        assertThat(reported.status()).isEqualTo(JobStatus.COMPLETED);
        assertThat(reported.result()).isEqualTo(resultPath.toString());
        assertThat(reported.errorMessage()).isNull();
    }

    @Test
    @DisplayName("예외 발생 시 JobResultRequest가 FAILED로 보고되어야 한다")
    void requestAndProcess_fail_execute() throws Exception {
        UUID orgId = UUID.randomUUID();
        UUID agentId = UUID.randomUUID();
        UUID datasourceId = UUID.randomUUID();
        UUID jobId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();

        ExecutionJob job = new ExecutionJob(jobId.toString(), "PENDING", "select 1", LocalDateTime.now(), LocalDateTime.now());

        when(queryServiceClient.requestQuery(eq(orgId), any(QueryRequest.class))).thenReturn(Optional.of(job));
        when(jobPollingService.poll(agentId, jobId)).thenReturn(job);
        when(jobExecutionService.execute(datasourceId, job.query(), jobId)).thenThrow(new RuntimeException("boom"));

        NaturalLanguageQueryResponse result = processor.handleQuery(
                NaturalLanguageQueryRequest.builder()
                        .naturalText("select 1")
                        .agentId(agentId)
                        .orgId(orgId)
                        .userId(userId)
                        .build()
        );

        assertThat(result.downloadUrl()).isEmpty();
        ArgumentCaptor<JobResultRequest> captor = ArgumentCaptor.forClass(JobResultRequest.class);
        verify(queryServiceClient).reportJobResult(eq(jobId), captor.capture());
        JobResultRequest reported = captor.getValue();
        assertThat(reported.status()).isEqualTo(JobStatus.FAILED);
        assertThat(reported.errorMessage()).isEqualTo("boom");
    }
}
