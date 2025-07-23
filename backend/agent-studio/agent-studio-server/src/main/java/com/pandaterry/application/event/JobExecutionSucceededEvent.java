package com.pandaterry.application.event;

import lombok.*;

import java.util.UUID;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class JobExecutionSucceededEvent implements JobExecutionEvent {
    private String authorization;
    private UUID jobId;
    private String downloadUrl;
}

