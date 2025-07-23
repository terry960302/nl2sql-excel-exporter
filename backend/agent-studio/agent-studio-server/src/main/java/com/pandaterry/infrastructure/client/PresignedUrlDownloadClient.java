package com.pandaterry.infrastructure.client;

import com.pandaterry.application.exception.AgentException;
import com.pandaterry.domain.enums.ErrorCode;
import io.micronaut.context.annotation.Requires;
import io.micronaut.context.annotation.Value;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.exceptions.HttpClientResponseException;
import io.micronaut.reactor.http.client.ReactorHttpClient;
import jakarta.inject.Singleton;
import reactor.core.publisher.Mono;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.time.Duration;

/**
 * 프리사인 URL을 이용해 파일을 다운로드하는 구현체.
 */
@Requires(property = "app.storage.type", value = "s3")
@Singleton
public class PresignedUrlDownloadClient implements DownloadClient {

    @Value("${app.storage.s3.bucket}")
    String bucket;
    @Value("${app.storage.s3.region}")
    String region;
    @Value("${app.storage.s3.base-path}")
    String basePath;
    @Value("${app.storage.s3.presign-expiration-seconds}")
    long expirationSeconds;

    private final S3Presigner presigner;
    private final ReactorHttpClient httpClient;

    public PresignedUrlDownloadClient(ReactorHttpClient httpClient, S3Presigner presigner) {
        this.httpClient = httpClient;
        this.presigner = presigner;
    }

    @Override
    public InputStream download(String filename) {
        String key = basePath + "/" + filename;

        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .build();

        GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
                .getObjectRequest(getObjectRequest)
                .signatureDuration(Duration.ofSeconds(expirationSeconds))
                .build();

        URL url = presigner.presignGetObject(presignRequest).url();
        return toInputStream(URI.create(url.toString())).block();
    }

    private Mono<ByteArrayInputStream> toInputStream(URI uri) {
        return httpClient
                .exchange(HttpRequest.GET(uri), byte[].class)
                .map(response -> new ByteArrayInputStream(response.body()))
                .onErrorMap(e -> new AgentException(ErrorCode.FILE_AWS_PRESIGNED_DOWNLOAD_FAILED, e));
    }
}
