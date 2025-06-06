package com.pandaterry.application.event;

public record JobExecutionSucceededEvent(String jobId, String downloadUrl) {
}
