package com.pandaterry.infrastructure.client;

import com.pandaterry.application.dto.request.RegisterSchemaRequest;
import com.pandaterry.domain.model.database.DatasourceSession;
import com.pandaterry.domain.model.database.TableSchema;
import io.micronaut.http.client.HttpClient;
import jakarta.inject.Singleton;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Singleton
public class SchemaServiceClient extends BaseServiceClient {
    protected SchemaServiceClient(HttpClient httpClient, String baseUrl, String agentSecret) {
        super(httpClient, baseUrl, agentSecret);
    }

    // 스키마 업로드
    public void uploadSchema(RegisterSchemaRequest req) {
        Map<String, String> header = new HashMap<>();
        header.put("X-Organization-Id", req.orgId().toString());
        post("/datasources", req, header, null);
    }
}
