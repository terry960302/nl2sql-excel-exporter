package com.pandaterry.application.service.query;

import com.pandaterry.domain.model.ExecutionJob;
import com.pandaterry.infrastructure.client.QueryServiceClient;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;
import java.util.UUID;

@Singleton
public class JobPollingService {

    private static final Logger log = LoggerFactory.getLogger(JobPollingService.class);

    @Inject
    private QueryServiceClient queryServiceClient;

    /**
     * 지정한 jobId가 할당된 작업을 polling
     * 일정 시간동안 polling 후에도 작업이 없으면 null을 반환
     */
    public ExecutionJob poll(UUID agentId, UUID jobId) {
        for (int i = 0; i < 5; i++) {
            Optional<ExecutionJob> opt = queryServiceClient.pollPendingJob(agentId);
            if (opt.isPresent() && opt.get().jobId().equals(jobId.toString())) {
                return opt.get();
            }
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
        log.warn("Job {} not found for agent {}", jobId, agentId);
        return null;
    }
}
