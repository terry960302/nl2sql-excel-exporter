package com.pandaterry.query_microservice.unit.application.service;

import com.pandaterry.msa_contracts.dto.query.request.JobResultRequest;
import com.pandaterry.msa_contracts.enums.query.JobStatus;
import com.pandaterry.query_microservice.application.service.JobService;
import com.pandaterry.query_microservice.domain.model.ExecutionJob;
import com.pandaterry.query_microservice.infrastructure.messaging.QuotaUsageProducer;
import com.pandaterry.msa_contracts.dto.quota.request.QuotaUsageRecordRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class JobServiceTest {
    private JobService jobService;
    private DummyProducer producer;

    @BeforeEach
    void setUp() {
        producer = new DummyProducer();
        jobService = new JobService(producer);
    }

    @Test
    void create_and_poll_job() {
        ExecutionJob created = jobService.createJob(UUID.randomUUID(), "select 1").block();
        assertNotNull(created);
        ExecutionJob polled = jobService.pollPendingJob(UUID.randomUUID()).block();
        assertEquals(created.jobId(), polled.jobId());
    }

    @Test
    void report_result_updates_status() {
        ExecutionJob created = jobService.createJob(UUID.randomUUID(), "select 1").block();
        jobService.reportResult(created.jobId(),
                new JobResultRequest(created.jobId(), JobStatus.COMPLETED, "ok", null)).block();
        ExecutionJob updated = jobService.getJob(created.jobId()).block();
        assertEquals(JobStatus.COMPLETED, updated.status());
    }

    static class DummyProducer implements QuotaUsageProducer {
        @Override
        public void sendQuotaUsage(QuotaUsageRecordRequest request) {
            // no-op for tests
        }
    }
}
