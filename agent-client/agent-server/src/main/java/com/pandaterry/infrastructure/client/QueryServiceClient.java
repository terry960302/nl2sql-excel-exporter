package com.pandaterry.infrastructure.client;

import com.pandaterry.domain.model.ExecutionJob;
import com.pandaterry.presentation.dto.request.JobResultRequest;
import com.pandaterry.presentation.dto.request.QueryRequest;
import io.micronaut.http.client.HttpClient;
import jakarta.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Map;

import java.util.Optional;
import java.util.UUID;

@Singleton
public class QueryServiceClient extends BaseServiceClient {
    private static final Logger logger = LoggerFactory.getLogger(QueryServiceClient.class);

    public QueryServiceClient(HttpClient httpClient, String baseUrl, String agentSecret) {
        super(httpClient, baseUrl, agentSecret);
    }

    // 자연어 질의 요청
    public Optional<ExecutionJob> requestQuery(UUID orgId, QueryRequest payload) {
        try {
            Map<String, String> headers = Map.of("X-Organization-Id", orgId.toString());
            ExecutionJob job = post("/api/v1/queries", payload, headers, ExecutionJob.class);
            return Optional.ofNullable(job);
        } catch (Exception e) {
            logger.error("Failed to request query", e);
            return Optional.empty();
        }
    }

    // 1. 작업 Polling
    public Optional<ExecutionJob> pollPendingJob(UUID agentId) {
        try {
            String path = "/jobs?agentId=" + agentId + "&status=PENDING";
            ExecutionJob job = get(path, ExecutionJob.class);
            return Optional.ofNullable(job);
        } catch (Exception e) {
            logger.error("Failed to poll job", e);
            return Optional.empty();
        }
    }

    // 2. 작업 결과 보고
    public void reportJobResult(UUID jobId, JobResultRequest payload) {
        String path = "/jobs/" + jobId + "/result";
        post(path, payload, null);
    }



}