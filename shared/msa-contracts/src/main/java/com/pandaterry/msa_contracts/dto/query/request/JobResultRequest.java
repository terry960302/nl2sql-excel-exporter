package com.pandaterry.msa_contracts.dto.query.request;

import com.pandaterry.msa_contracts.enums.query.JobStatus;

public record JobResultRequest(
        String jobId,
        JobStatus status,
        String result,
        String errorMessage
) {}
