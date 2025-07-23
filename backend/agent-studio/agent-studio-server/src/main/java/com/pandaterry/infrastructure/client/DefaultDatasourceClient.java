package com.pandaterry.infrastructure.client;

import com.pandaterry.msa_contracts.constants.HeaderKeys;
import com.pandaterry.msa_contracts.constants.RoutePath;
import com.pandaterry.msa_contracts.dto.schema.response.DatasourceResponse;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Header;
import io.micronaut.http.annotation.Headers;
import io.micronaut.http.client.annotation.Client;
import reactor.core.publisher.Flux;

import java.util.List;

@Client(id = "gateway", path = RoutePath.Datasource.BASE)
public interface DefaultDatasourceClient extends DatasourceClient {
    @Get
    @Override
    List<DatasourceResponse> getDatasources(@Header(HeaderKeys.AUTHORIZATION) String authorization);
}
