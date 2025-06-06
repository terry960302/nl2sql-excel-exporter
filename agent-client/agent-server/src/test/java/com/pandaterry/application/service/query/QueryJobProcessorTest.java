package com.pandaterry.application.service.query;

import com.pandaterry.application.event.JobExecutionEvent;
import com.pandaterry.application.event.JobExecutionFailedEvent;
import com.pandaterry.application.event.JobExecutionSucceededEvent;
import com.pandaterry.application.exception.AgentException;
import com.pandaterry.application.facade.QueryJobProcessFacade;
import com.pandaterry.application.vo.ExcelResult;
import com.pandaterry.domain.enums.ErrorCode;
import com.pandaterry.msa_contracts.dto.query.request.JobResultRequest;
import com.pandaterry.msa_contracts.dto.query.request.NaturalLanguageQueryRequest;
import com.pandaterry.msa_contracts.dto.query.response.NaturalLanguageQueryResponse;
import com.pandaterry.presentation.dto.request.QueryRequest;
import com.pandaterry.msa_contracts.enums.query.JobStatus;
import com.pandaterry.domain.model.ExecutionJob;
import com.pandaterry.infrastructure.client.QueryServiceClient;
import io.micronaut.context.event.ApplicationEventPublisher;
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
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class QueryJobProcessorTest {

        @Mock
        private QueryServiceClient queryServiceClient;
        @Mock
        private JobPollingService jobPollingService;
        @Mock
        private JobExecutionService jobExecutionService;

        @Mock
        ApplicationEventPublisher<JobExecutionEvent> jobExecutionEventPublisher;
        @InjectMocks
        private QueryJobProcessFacade processor;

        @Test
        @DisplayName("정상적인 Job 처리 시 결과 파일 경로가 반환되고, 성공 이벤트가 발행되어야 한다")
        void requestAndProcess_success() throws Exception {
                UUID orgId = UUID.randomUUID();
                UUID agentId = UUID.randomUUID();
                UUID datasourceId = UUID.randomUUID();
                UUID jobId = UUID.randomUUID();
                UUID userId = UUID.randomUUID();

                ExecutionJob job = new ExecutionJob(jobId, JobStatus.PENDING, orgId, "select 1",
                        LocalDateTime.now(), LocalDateTime.now());

                when(queryServiceClient.requestQuery(eq(orgId), any(QueryRequest.class)))
                        .thenReturn(Optional.of(job));
                when(jobPollingService.poll(agentId, jobId)).thenReturn(job);

                Path resultPath = Path.of("result.xlsx");
                String filename = resultPath.getFileName().toString();
                String url = resultPath.toString();

                when(jobExecutionService.execute(datasourceId, job.query(), jobId))
                        .thenReturn(new ExcelResult(filename, url));
                // 더 이상 reportJobResult를 직접 호출하지 않으므로 스텁 제거 후, 대신 이벤트 퍼블리시만 스텁 처리
                doNothing().when(jobExecutionEventPublisher).publishEvent(any(JobExecutionSucceededEvent.class));

                // 실제 호출
                NaturalLanguageQueryResponse result = processor.handleQuery(
                        NaturalLanguageQueryRequest.builder()
                                .naturalText("select 1")
                                .agentId(agentId)
                                .orgId(orgId)
                                .userId(userId)
                                .datasourceId(datasourceId)
                                .build());
                // 응답 검증
                assertThat(result.downloadUrl()).contains(url);

                // 이벤트 발행 여부 검증
                ArgumentCaptor<JobExecutionSucceededEvent> eventCaptor = ArgumentCaptor.forClass(JobExecutionSucceededEvent.class);
                verify(jobExecutionEventPublisher, times(1)).publishEvent(eventCaptor.capture());

                JobExecutionSucceededEvent published = eventCaptor.getValue();
                assertThat(published.getJobId()).isEqualTo(jobId);
                assertThat(published.getDownloadUrl()).isEqualTo(url);
        }


        @Test
        @DisplayName("예외 발생 시 JobResultRequest가 FAILED로 보고되어야 한다")
        void requestAndProcess_fail_execute() throws Exception {
                UUID orgId = UUID.randomUUID();
                UUID agentId = UUID.randomUUID();
                UUID datasourceId = UUID.randomUUID();
                UUID jobId = UUID.randomUUID();
                UUID userId = UUID.randomUUID();

                ExecutionJob job = new ExecutionJob(jobId, JobStatus.PENDING, orgId, "select 1", LocalDateTime.now(),
                                LocalDateTime.now());

                when(queryServiceClient.requestQuery(eq(orgId), any(QueryRequest.class))).thenReturn(Optional.of(job));
                when(jobPollingService.poll(agentId, jobId)).thenReturn(job);
                when(jobExecutionService.execute(datasourceId, job.query(), jobId))
                                .thenThrow(new RuntimeException("boom"));
                doNothing().when(jobExecutionEventPublisher).publishEvent(any(JobExecutionFailedEvent.class));

                assertThatThrownBy(() -> processor.handleQuery(
                                NaturalLanguageQueryRequest.builder()
                                                .naturalText("select 1")
                                                .agentId(agentId)
                                                .orgId(orgId)
                                                .userId(userId)
                                                .datasourceId(datasourceId)
                                                .build()))
                                .isInstanceOf(AgentException.class)
                                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.INTERNAL_SERVER_ERROR);

                ArgumentCaptor<JobExecutionFailedEvent> eventCaptor = ArgumentCaptor.forClass(JobExecutionFailedEvent.class);
                verify(jobExecutionEventPublisher, times(1)).publishEvent(eventCaptor.capture());
                JobExecutionFailedEvent published = eventCaptor.getValue();
                assertThat(published.getJobId()).isEqualTo(jobId);
                assertThat(published.getReason()).isEqualTo("boom");
        }
}
