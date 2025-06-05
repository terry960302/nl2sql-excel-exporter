package com.pandaterry.infrastructure.client;

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
        header.put("X-Organization-Id", req.orgId().toString());
        header.put("X-User-Id", req.userId().toString());
        header.put("X-Agent-Id", req.agentId().toString());

        return post("/schemas", req, header, null);
    }
}
