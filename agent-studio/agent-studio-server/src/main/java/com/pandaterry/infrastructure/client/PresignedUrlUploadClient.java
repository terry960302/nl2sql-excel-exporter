package com.pandaterry.infrastructure.client;

import io.micronaut.context.annotation.Requires;
import io.micronaut.context.annotation.Value;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.MediaType;
import io.micronaut.http.client.HttpClient;
import jakarta.inject.Singleton;

import java.nio.file.Files;
import java.nio.file.Path;

/**
 * 프리사인 URL을 이용해 파일을 업로드하는 기본 구현체.
 */
@Requires(property = "storage.type", value = "s3")
@Singleton
public class PresignedUrlUploadClient implements UploadClient {

    private final HttpClient httpClient;
    private final String presignedPutUrl;
    private final String downloadUrl;

    public PresignedUrlUploadClient(HttpClient httpClient,
                                    @Value("${storage.presigned-put-url}") String presignedPutUrl,
                                    @Value("${storage.download-url}") String downloadUrl) {
        this.httpClient = httpClient;
        this.presignedPutUrl = presignedPutUrl;
        this.downloadUrl = downloadUrl;
    }

    @Override
    public String upload(Path filePath) throws Exception {
        byte[] bytes = Files.readAllBytes(filePath);
        HttpRequest<byte[]> req = HttpRequest.PUT(presignedPutUrl, bytes)
                .contentType(MediaType.APPLICATION_OCTET_STREAM);
        httpClient.toBlocking().exchange(req);
        return downloadUrl;
    }
}
