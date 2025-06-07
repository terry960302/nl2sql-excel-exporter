package com.pandaterry.application.event;

import lombok.*;

import java.util.UUID;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class JobExecutionSucceededEvent implements JobExecutionEvent {
    private UUID jobId;
    private String downloadUrl;
}

