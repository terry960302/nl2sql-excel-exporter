package com.pandaterry.application.event;

public record JobExecutionFailedEvent(String jobId, String reason) {
}
