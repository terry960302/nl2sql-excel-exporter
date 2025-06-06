package com.pandaterry.msa_contracts.dto.query.request;

import com.pandaterry.msa_contracts.enums.query.JobStatus;

import java.util.UUID;

public record JobResultRequest(
        UUID jobId,
        JobStatus status,
        String result,
        String errorMessage
) {}
