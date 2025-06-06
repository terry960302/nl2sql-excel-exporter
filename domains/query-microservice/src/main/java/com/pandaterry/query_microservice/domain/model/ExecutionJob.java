package com.pandaterry.query_microservice.domain.model;

import com.pandaterry.msa_contracts.enums.query.JobStatus;

import java.time.LocalDateTime;

public record ExecutionJob(
        String jobId,
        JobStatus status,
        String query,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {}
