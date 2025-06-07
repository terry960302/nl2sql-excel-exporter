package com.pandaterry.infrastructure.client;

import io.micronaut.context.annotation.Requires;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.exceptions.HttpClientResponseException;
import jakarta.inject.Singleton;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.URI;

/**
 * 프리사인 URL을 이용해 파일을 다운로드하는 구현체.
 */
@Requires(property = "storage.type", value = "s3")
@Singleton
public class PresignedUrlDownloadClient implements DownloadClient {

    private final HttpClient httpClient;

    public PresignedUrlDownloadClient(HttpClient httpClient) {
        this.httpClient = httpClient;
    }

    @Override
    public InputStream download(String url) throws Exception {
        URI uri = URI.create(url);
        try {
            byte[] data = httpClient.toBlocking().retrieve(HttpRequest.GET(uri), byte[].class);
            return new ByteArrayInputStream(data);
        } catch (HttpClientResponseException e) {
            throw new RuntimeException("Failed to download file: " + e.getMessage(), e);
        }
    }
}
