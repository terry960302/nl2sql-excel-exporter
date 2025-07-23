package com.pandaterry.infrastructure.client;

import com.pandaterry.msa_contracts.constants.HeaderKeys;
import com.pandaterry.msa_contracts.constants.RoutePath;
import com.pandaterry.msa_contracts.dto.schema.request.RegisterSchemaRequest;
import com.pandaterry.msa_contracts.dto.schema.response.RegisterSchemaResponse;
import io.micronaut.http.annotation.Header;
import io.micronaut.http.annotation.Post;
import io.micronaut.http.client.annotation.Client;

@Client(id = "gateway", path = RoutePath.Schema.BASE)
public interface DefaultSchemaClient extends SchemaClient{
    @Post
    @Override
    RegisterSchemaResponse uploadSchema(@Header(HeaderKeys.AUTHORIZATION) String authorization, RegisterSchemaRequest request);
}
