package com.pandaterry.query_microservice.application.dto.request;

public record JobResultRequest(
        String jobId,
        String status,
        String result,
        String errorMessage
) {}
