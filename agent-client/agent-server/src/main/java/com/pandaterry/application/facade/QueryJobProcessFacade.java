package com.pandaterry.application.facade;

import com.pandaterry.application.event.JobExecutionFailedEvent;
import com.pandaterry.application.event.JobExecutionSucceededEvent;
import com.pandaterry.application.exception.AgentException;
import com.pandaterry.application.service.query.JobExecutionService;
import com.pandaterry.application.service.query.JobPollingService;
import com.pandaterry.application.service.query.JobResultEventListener;
import com.pandaterry.application.vo.ExcelResult;
import com.pandaterry.domain.enums.ErrorCode;
import com.pandaterry.msa_contracts.dto.query.request.NaturalLanguageQueryRequest;
import com.pandaterry.msa_contracts.dto.query.response.NaturalLanguageQueryResponse;
import com.pandaterry.presentation.dto.request.QueryRequest;
import com.pandaterry.msa_contracts.enums.query.JobStatus;
import com.pandaterry.domain.model.ExecutionJob;
import com.pandaterry.infrastructure.client.QueryServiceClient;
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
    private QueryServiceClient queryServiceClient;
    @Inject
    private JobPollingService jobPollingService;
    @Inject
    private JobExecutionService jobExecutionService;

    @Inject
    private JobResultEventListener jobResultEventListener;

    public NaturalLanguageQueryResponse handleQuery(NaturalLanguageQueryRequest request) {
        return requestQuery(request.getOrgId(), request.getNaturalText())
                .flatMap(jobId -> pollJob(request.getAgentId(), jobId))
                .flatMap(job -> executeJob(job, request.getDatasourceId()))
                .orElseThrow(() -> new AgentException(ErrorCode.INTERNAL_SERVER_ERROR));
    }

    private Optional<UUID> requestQuery(UUID orgId, String naturalText) {
        Optional<ExecutionJob> created = queryServiceClient.requestQuery(orgId, new QueryRequest(naturalText));
        if (created.isEmpty()) {
            log.warn("Query request failed for orgId={}", orgId);
            return Optional.empty();
        }
        return Optional.of(UUID.fromString(created.get().jobId()));
    }

    private Optional<ExecutionJob> pollJob(UUID agentId, UUID jobId) {
        ExecutionJob job = jobPollingService.poll(agentId, jobId);
        if (job == null) {
            log.warn("Job polling failed. jobId={}, agentId={}", jobId, agentId);
            return Optional.empty();
        }
        return Optional.of(job);
    }

    private Optional<NaturalLanguageQueryResponse> executeJob(ExecutionJob job, UUID datasourceId) {
        try {
            ExcelResult excelResult = jobExecutionService.execute(datasourceId, job.query(), UUID.fromString(job.jobId()));
            if (excelResult == null) {
                log.warn("Job executed but empty result. jobId={}", job.jobId());

                return Optional.empty();
            }
            jobResultEventListener.handleSuccess(new JobExecutionSucceededEvent(job.jobId(), excelResult.downloadUrl()));
            return Optional.of(new NaturalLanguageQueryResponse(excelResult.filename(), excelResult.downloadUrl()));
        } catch (Exception e) {
            log.error("Failed to execute jobId={}", job.jobId(), e);
            jobResultEventListener.handleFailure(new JobExecutionFailedEvent(job.jobId(), e.getMessage()));
            return Optional.empty();
        }
    }
}
