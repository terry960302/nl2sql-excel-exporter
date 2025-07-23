package com.pandaterry.infrastructure.client;

import com.pandaterry.msa_contracts.constants.HeaderKeys;
import com.pandaterry.msa_contracts.dto.schema.request.RegisterSchemaRequest;
import com.pandaterry.msa_contracts.dto.schema.response.RegisterSchemaResponse;
import io.micronaut.http.annotation.Header;

public interface SchemaClient {
    RegisterSchemaResponse uploadSchema(@Header(HeaderKeys.AUTHORIZATION) String authorization, RegisterSchemaRequest request);
}
