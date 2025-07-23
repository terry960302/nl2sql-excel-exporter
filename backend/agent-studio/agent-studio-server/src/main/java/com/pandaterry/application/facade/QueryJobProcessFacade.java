package com.pandaterry.application.facade;

import com.pandaterry.application.event.JobExecutionEvent;
import com.pandaterry.application.event.JobExecutionFailedEvent;
import com.pandaterry.application.event.JobExecutionSucceededEvent;
import com.pandaterry.application.exception.AgentException;
import com.pandaterry.application.service.query.JobExecutionService;
import com.pandaterry.application.service.query.JobPollingService;
import com.pandaterry.application.vo.ExcelResult;
import com.pandaterry.domain.enums.ErrorCode;
import com.pandaterry.infrastructure.client.DefaultQueryClient;
import com.pandaterry.msa_contracts.dto.query.request.NaturalLanguageQueryRequest;
import com.pandaterry.msa_contracts.dto.query.response.NaturalLanguageQueryResponse;
import com.pandaterry.presentation.dto.request.QueryRequest;
import com.pandaterry.domain.model.ExecutionJob;
import io.micronaut.context.event.ApplicationEventPublisher;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;
import java.util.UUID;

@Singleton
public class QueryJobProcessFacade {

    private static final Logger log = LoggerFactory.getLogger(QueryJobProcessFacade.class);

    @Inject
    private DefaultQueryClient queryClient;
    @Inject
    private JobPollingService jobPollingService;
    @Inject
    private JobExecutionService jobExecutionService;

    @Inject
    private ApplicationEventPublisher<JobExecutionEvent> jobExecutionEventPublisher;

    public NaturalLanguageQueryResponse handleQuery(String authorization, NaturalLanguageQueryRequest request) {
        return requestQuery(authorization, request)
                .flatMap(jobId -> pollJob(authorization, jobId))
                .flatMap(job -> executeJob(job, request.getDatasourceId()))
                .orElseThrow(() -> new AgentException(ErrorCode.INTERNAL_SERVER_ERROR));
    }

    private Optional<UUID> requestQuery(String authorization, NaturalLanguageQueryRequest request) {
        Optional<ExecutionJob> created = queryClient.requestQuery(authorization, request);
        if (created.isEmpty()) {
            log.warn("Query request failed");
            return Optional.empty();
        }
        return Optional.of(created.get().jobId());
    }

    private Optional<ExecutionJob> pollJob(String authorization, UUID jobId) {
        ExecutionJob job = jobPollingService.poll(authorization, jobId);
        if (job == null) {
            log.warn("Job polling failed. jobId={}", jobId);
            return Optional.empty();
        }
        return Optional.of(job);
    }

    private Optional<NaturalLanguageQueryResponse> executeJob(ExecutionJob job, UUID datasourceId) {
        try {
            ExcelResult excelResult = jobExecutionService.execute(datasourceId, job.query(), job.jobId());
            if (excelResult == null) {
                log.warn("Job executed but empty result. jobId={}", job.jobId());

                return Optional.empty();
            }
            jobExecutionEventPublisher.publishEvent(JobExecutionSucceededEvent.builder()
                    .jobId(job.jobId())
                    .downloadUrl(excelResult.downloadUrl())
                    .build());
            return Optional.of(new NaturalLanguageQueryResponse(excelResult.filename(), excelResult.downloadUrl()));
        } catch (Exception e) {
            log.error("Failed to execute jobId={}", job.jobId(), e);
            jobExecutionEventPublisher.publishEvent(JobExecutionFailedEvent.builder()
                    .jobId(job.jobId())
                    .reason(e.getMessage())
                    .build());
            return Optional.empty();
        }
    }
}
