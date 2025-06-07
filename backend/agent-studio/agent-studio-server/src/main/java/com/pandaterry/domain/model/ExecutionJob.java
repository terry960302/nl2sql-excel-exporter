package com.pandaterry.domain.model;

import com.pandaterry.msa_contracts.enums.query.JobStatus;

import java.time.LocalDateTime;
import java.util.UUID;

public record ExecutionJob(
                UUID jobId,
                JobStatus status,
                UUID orgId,
                String query,
                LocalDateTime createdAt,
                LocalDateTime updatedAt) {
}