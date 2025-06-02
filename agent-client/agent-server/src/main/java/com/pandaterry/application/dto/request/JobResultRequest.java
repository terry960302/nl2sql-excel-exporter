package com.pandaterry.application.dto.request;

public record JobResultRequest(
                String jobId,
                String status,
                String result,
                String errorMessage) {
}