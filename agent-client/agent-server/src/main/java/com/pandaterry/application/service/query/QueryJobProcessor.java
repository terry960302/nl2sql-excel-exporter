package com.pandaterry.application.service.query;

import com.pandaterry.application.dto.request.JobResultRequest;
import com.pandaterry.application.dto.request.QueryRequest;
import com.pandaterry.domain.enums.JobStatus;
import com.pandaterry.domain.model.ExecutionJob;
import com.pandaterry.infrastructure.client.QueryServiceClient;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;
import java.util.Optional;
import java.util.UUID;

@Singleton
public class QueryJobProcessor {

    private static final Logger log = LoggerFactory.getLogger(QueryJobProcessor.class);

    @Inject
    private QueryServiceClient queryServiceClient;
    @Inject
    private JobPollingService jobPollingService;
    @Inject
    private JobExecutionService jobExecutionService;

    public Optional<Path> requestAndProcess(UUID orgId, UUID agentId, UUID datasourceId, String naturalText) {
        Optional<ExecutionJob> created = queryServiceClient.requestQuery(orgId, new QueryRequest(naturalText));
        if (created.isEmpty()) {
            return Optional.empty();
        }
        UUID jobId = UUID.fromString(created.get().jobId());
        ExecutionJob job = jobPollingService.poll(agentId, jobId);
        if (job == null) {
            return Optional.empty();
        }
        try {
            Path excel = jobExecutionService.execute(datasourceId, job.query(), jobId);
            if (excel == null) {
                queryServiceClient.reportJobResult(jobId, new JobResultRequest(job.jobId(), JobStatus.FAILED, null, "empty result"));
                return Optional.empty();
            }
            queryServiceClient.reportJobResult(jobId, new JobResultRequest(job.jobId(), JobStatus.COMPLETED, excel.toString(), null));
            return Optional.of(excel);
        } catch (Exception e) {
            log.error("failed to execute job", e);
            queryServiceClient.reportJobResult(jobId, new JobResultRequest(job.jobId(), JobStatus.FAILED, null, e.getMessage()));
            return Optional.empty();
        }
    }
}
