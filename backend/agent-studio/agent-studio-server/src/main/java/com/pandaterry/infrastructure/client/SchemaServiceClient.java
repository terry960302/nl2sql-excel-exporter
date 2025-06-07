package com.pandaterry.infrastructure.client;

import com.pandaterry.msa_contracts.constants.HeaderKeys;
import com.pandaterry.msa_contracts.dto.schema.request.RegisterSchemaRequest;
import com.pandaterry.msa_contracts.dto.schema.response.RegisterSchemaResponse;
import io.micronaut.http.client.HttpClient;
import jakarta.inject.Singleton;

import java.util.HashMap;
import java.util.Map;

@Singleton
public class SchemaServiceClient extends BaseServiceClient {
    public SchemaServiceClient(HttpClient httpClient, String baseUrl, String agentSecret) {
        super(httpClient, baseUrl, agentSecret);
    }

    // 스키마 업로드
    public RegisterSchemaResponse uploadSchema(RegisterSchemaRequest req) {
        Map<String, String> header = new HashMap<>();
        header.put(HeaderKeys.ORG_ID, req.orgId().toString());
        header.put(HeaderKeys.USER_ID, req.userId().toString());
        header.put(HeaderKeys.AGENT_ID, req.agentId().toString());

        return post("/schemas", req, header, null);
    }
}
