package com.pandaterry.infrastructure.client;

import com.pandaterry.msa_contracts.constants.HeaderKeys;
import com.pandaterry.msa_contracts.dto.schema.response.DatasourceResponse;
import io.micronaut.http.annotation.Header;
import reactor.core.publisher.Flux;

import java.util.List;

public interface DatasourceClient {
    List<DatasourceResponse> getDatasources(@Header(HeaderKeys.AUTHORIZATION) String authorization);
}
