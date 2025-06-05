package com.pandaterry.infrastructure.config;

import com.pandaterry.infrastructure.client.QueryServiceClient;
import com.pandaterry.infrastructure.client.SchemaServiceClient;
import io.micronaut.context.annotation.Factory;
import io.micronaut.context.annotation.Value;
import io.micronaut.http.client.HttpClient;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;

@Factory
public class ServiceClientFactory {

    @Value("${services.gateway.base-url}")
    private String gatewayBaseUrl;

    @Value("${agent.secret}")
    private String agentSecret;

    @Inject
    private HttpClient httpClient;

    @Singleton
    public SchemaServiceClient schemaServiceClient() {
        return new SchemaServiceClient(httpClient, gatewayBaseUrl, agentSecret);
    }

    @Singleton
    public QueryServiceClient queryServiceClient() {
        return new QueryServiceClient(httpClient, gatewayBaseUrl, agentSecret);
    }
}
