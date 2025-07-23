package com.pandaterry.infrastructure.client;

import com.pandaterry.domain.model.ExecutionJob;
import com.pandaterry.msa_contracts.constants.HeaderKeys;
import com.pandaterry.msa_contracts.dto.query.request.JobResultRequest;
import com.pandaterry.msa_contracts.dto.query.request.NaturalLanguageQueryRequest;
import io.micronaut.http.annotation.Header;

import java.util.Optional;
import java.util.UUID;

public interface QueryClient {
    Optional<ExecutionJob> requestQuery(@Header(HeaderKeys.AUTHORIZATION) String authorization, NaturalLanguageQueryRequest request);
    Optional<ExecutionJob> pollPendingJob(@Header(HeaderKeys.AUTHORIZATION) String authorization);
    Void reportJobResult(@Header(HeaderKeys.AUTHORIZATION) String authorization, UUID jobId, JobResultRequest request);
}
