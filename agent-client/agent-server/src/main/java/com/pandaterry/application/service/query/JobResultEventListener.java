package com.pandaterry.application.service.query;

import com.pandaterry.application.event.JobExecutionFailedEvent;
import com.pandaterry.application.event.JobExecutionSucceededEvent;
import com.pandaterry.msa_contracts.dto.query.request.JobResultRequest;
import com.pandaterry.msa_contracts.enums.query.JobStatus;
import com.pandaterry.infrastructure.client.QueryServiceClient;
import io.micronaut.runtime.event.annotation.EventListener;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;

@Singleton
public class JobResultEventListener {

    private static final Logger log = LoggerFactory.getLogger(JobResultEventListener.class);

    @Inject
    private QueryServiceClient queryServiceClient;

    @EventListener
    public void handleSuccess(JobExecutionSucceededEvent event) {
        String jobIdStr = event.jobId();
        String downloadUrl = event.downloadUrl();
        log.info("Handling JobExecutionSucceededEvent. jobId={}, downloadUrl={}", jobIdStr, downloadUrl);

        try {
            queryServiceClient.reportJobResult(
                    UUID.fromString(jobIdStr),
                    new JobResultRequest(jobIdStr, JobStatus.COMPLETED, downloadUrl, null)
            );
        } catch (Exception e) {
            log.error("Failed to report success for jobId={}", jobIdStr, e);
        }
    }

    @EventListener
    public void handleFailure(JobExecutionFailedEvent event) {
        String jobIdStr = event.jobId();
        String reason = event.reason();
        log.info("Handling JobExecutionFailedEvent. jobId={}, reason={}", jobIdStr, reason);

        try {
            queryServiceClient.reportJobResult(
                    UUID.fromString(jobIdStr),
                    new JobResultRequest(jobIdStr, JobStatus.FAILED, null, reason)
            );
        } catch (Exception e) {
            log.error("Failed to report failure for jobId={}", jobIdStr, e);
        }
    }
}