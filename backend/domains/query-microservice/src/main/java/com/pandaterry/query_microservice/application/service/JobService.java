package com.pandaterry.query_microservice.application.service;

import com.pandaterry.msa_contracts.dto.query.request.JobResultRequest;
import com.pandaterry.msa_contracts.enums.query.JobStatus;
import com.pandaterry.msa_contracts.event.QuotaUsageEvent;
import com.pandaterry.query_microservice.domain.model.ExecutionJob;
import com.pandaterry.query_microservice.infrastructure.messaging.QuotaUsageProducer;
import com.pandaterry.msa_contracts.dto.quota.request.QuotaUsageRecordRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
public class JobService {
    private final Map<UUID, ExecutionJob> store = new ConcurrentHashMap<>();
    private final QuotaUsageProducer quotaUsageProducer;

    public Mono<ExecutionJob> createJob(UUID orgId, UUID userId, String query) {
        UUID id = UUID.randomUUID();
        ExecutionJob job = new ExecutionJob(id, JobStatus.PENDING, orgId, userId, query, LocalDateTime.now(), LocalDateTime.now());
        store.put(id, job);
        return Mono.just(job);
    }

    public Mono<ExecutionJob> pollPendingJob(UUID agentId) {
        return Mono.justOrEmpty(
                store.values().stream()
                        .filter(j -> JobStatus.PENDING.equals(j.status()))
                        .findFirst()
        );
    }

    public Mono<Void> reportResult(UUID jobId, JobResultRequest request) {
        ExecutionJob existing = store.get(jobId);
        if (existing != null) {
            ExecutionJob updated = new ExecutionJob(
                    existing.jobId(),
                    request.status(),
                    existing.orgId(),
                    existing.userId(),
                    existing.query(),
                    existing.createdAt(),
                    LocalDateTime.now()
            );
            store.put(existing.jobId(), updated);
            if (JobStatus.COMPLETED.equals(request.status())) {
                quotaUsageProducer.sendQuotaUsage(
                        new QuotaUsageEvent(existing.orgId(), existing.userId(), 1)
                );
            }
        }
        return Mono.empty();
    }

    public Mono<ExecutionJob> getJob(UUID jobId) {
        return Mono.justOrEmpty(store.get(jobId));
    }
}
