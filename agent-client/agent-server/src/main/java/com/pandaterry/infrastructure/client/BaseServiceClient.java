package com.pandaterry.infrastructure.client;

import io.micronaut.http.HttpRequest;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.uri.UriBuilder;
import jakarta.annotation.Nullable;

public abstract class BaseServiceClient {

    protected final HttpClient httpClient;
    protected final String baseUrl;
    protected final String agentSecret;

    protected BaseServiceClient(HttpClient httpClient, String baseUrl, String agentSecret) {
        this.httpClient = httpClient;
        this.baseUrl = baseUrl;
        this.agentSecret = agentSecret;
    }

    protected <T> T get(String path, Class<T> responseType) {
        HttpRequest<?> request = HttpRequest.GET(UriBuilder.of(baseUrl).path(path).build())
                .bearerAuth(agentSecret);

        return httpClient.toBlocking().retrieve(request, responseType);
    }

    protected <T> void post(String path, Object body, @Nullable Class<T> responseType) {
        HttpRequest<?> request = HttpRequest.POST(UriBuilder.of(baseUrl).path(path).build(), body)
                .bearerAuth(agentSecret);

        if (responseType != null) {
            httpClient.toBlocking().retrieve(request, responseType);
        } else {
            httpClient.toBlocking().exchange(request);
        }
    }
}
