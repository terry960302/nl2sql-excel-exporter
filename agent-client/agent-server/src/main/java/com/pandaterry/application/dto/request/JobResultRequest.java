package com.pandaterry.application.dto.request;

import com.pandaterry.domain.enums.JobStatus;

public record JobResultRequest(
                String jobId,
                JobStatus status,
                String result,
                String errorMessage) {
}