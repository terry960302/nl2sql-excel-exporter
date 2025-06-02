package com.pandaterry.infrastructure.client;

import com.pandaterry.domain.model.database.DatabaseConnection;
import io.micronaut.http.client.HttpClient;
import jakarta.inject.Singleton;

import java.util.UUID;

@Singleton
public class SchemaServiceClient extends BaseServiceClient{
    protected SchemaServiceClient(HttpClient httpClient, String baseUrl, String agentSecret) {
        super(httpClient, baseUrl, agentSecret);
    }

    // 스키마 업로드
    public void uploadSchema(UUID orgId, DatabaseConnection connection) {
        post("/datasources", connection, null);
    }
}
