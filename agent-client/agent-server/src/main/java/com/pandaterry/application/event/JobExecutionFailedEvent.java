package com.pandaterry.application.event;

import lombok.*;

import java.util.UUID;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class JobExecutionFailedEvent implements JobExecutionEvent {
    private UUID jobId;
    private String reason;
}

