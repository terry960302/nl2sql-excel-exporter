package com.pandaterry.infrastructure.client;

import com.pandaterry.domain.model.ExecutionJob;
import com.pandaterry.msa_contracts.constants.RoutePath;
import com.pandaterry.msa_contracts.dto.query.request.JobResultRequest;
import com.pandaterry.msa_contracts.dto.query.request.NaturalLanguageQueryRequest;
import io.micronaut.http.annotation.Post;
import io.micronaut.http.client.annotation.Client;

import java.util.Optional;
import java.util.UUID;

@Client(id = "gateway")
public interface DefaultQueryClient extends QueryClient{
    @Post(RoutePath.Query.EXECUTE)
    @Override
    Optional<ExecutionJob> requestQuery(String authorization, NaturalLanguageQueryRequest request);

    @Post(RoutePath.Job.BASE)
    @Override
    Optional<ExecutionJob> pollPendingJob(String authorization);

    @Post(RoutePath.Job.DETAIL_RESULT)
    @Override
    Void reportJobResult(String authorization, UUID jobId, JobResultRequest request);
}
