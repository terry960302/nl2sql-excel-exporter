package com.pandaterry.query_microservice.domain.model;

import java.time.LocalDateTime;

public record ExecutionJob(
        String jobId,
        String status,
        String query,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {}
