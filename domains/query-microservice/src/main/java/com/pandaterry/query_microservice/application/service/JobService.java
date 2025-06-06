package com.pandaterry.query_microservice.application.service;

import com.pandaterry.msa_contracts.dto.query.request.JobResultRequest;
import com.pandaterry.msa_contracts.enums.query.JobStatus;
import com.pandaterry.query_microservice.domain.model.ExecutionJob;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class JobService {
    private final Map<String, ExecutionJob> store = new ConcurrentHashMap<>();

    public Mono<ExecutionJob> createJob(String query) {
        String id = UUID.randomUUID().toString();
        ExecutionJob job = new ExecutionJob(id, JobStatus.PENDING, query, LocalDateTime.now(), LocalDateTime.now());
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
        ExecutionJob existing = store.get(jobId.toString());
        if (existing != null) {
            ExecutionJob updated = new ExecutionJob(
                    existing.jobId(),
                    request.status(),
                    existing.query(),
                    existing.createdAt(),
                    LocalDateTime.now()
            );
            store.put(existing.jobId(), updated);
        }
        return Mono.empty();
    }

    public Mono<ExecutionJob> getJob(String jobId) {
        return Mono.justOrEmpty(store.get(jobId));
    }
}
