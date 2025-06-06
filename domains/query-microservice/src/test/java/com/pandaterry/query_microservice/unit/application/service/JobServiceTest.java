package com.pandaterry.query_microservice.unit.application.service;

import com.pandaterry.msa_contracts.dto.query.request.JobResultRequest;
import com.pandaterry.msa_contracts.enums.query.JobStatus;
import com.pandaterry.query_microservice.application.service.JobService;
import com.pandaterry.query_microservice.domain.model.ExecutionJob;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class JobServiceTest {
    private JobService jobService;

    @BeforeEach
    void setUp() {
        jobService = new JobService();
    }

    @Test
    void create_and_poll_job() {
        ExecutionJob created = jobService.createJob("select 1").block();
        assertNotNull(created);
        ExecutionJob polled = jobService.pollPendingJob(UUID.randomUUID()).block();
        assertEquals(created.jobId(), polled.jobId());
    }

    @Test
    void report_result_updates_status() {
        ExecutionJob created = jobService.createJob("select 1").block();
        jobService.reportResult(UUID.fromString(created.jobId()),
                new JobResultRequest(created.jobId(), JobStatus.COMPLETED, "ok", null)).block();
        ExecutionJob updated = jobService.getJob(created.jobId()).block();
        assertEquals(JobStatus.COMPLETED, updated.status());
    }
}
