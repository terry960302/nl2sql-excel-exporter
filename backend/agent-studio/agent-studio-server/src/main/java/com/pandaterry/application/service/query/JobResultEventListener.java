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
    public void onSuccess(JobExecutionSucceededEvent event) {
        UUID jobId = event.getJobId();
        String downloadUrl = event.getDownloadUrl();
        log.info("Handling JobExecutionSucceededEvent. jobId={}, downloadUrl={}", jobId, downloadUrl);

        try {
            queryServiceClient.reportJobResult(
                    jobId,
                    new JobResultRequest(jobId, JobStatus.COMPLETED, downloadUrl, null)
            );
        } catch (Exception e) {
            log.error("Failed to report success for jobId={}", jobId, e);
        }
    }

    @EventListener
    public void onFailure(JobExecutionFailedEvent event) {
        UUID jobId = event.getJobId();

        String reason = event.getReason();
        log.info("Handling JobExecutionFailedEvent. jobId={}, reason={}", jobId, reason);

        try {
            queryServiceClient.reportJobResult(
                    jobId,
                    new JobResultRequest(jobId, JobStatus.FAILED, null, reason)
            );
        } catch (Exception e) {
            log.error("Failed to report failure for jobId={}", jobId, e);
        }
    }
}