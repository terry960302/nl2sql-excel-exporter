package com.pandaterry.infrastructure.client;

import io.micronaut.context.annotation.Requires;
import io.micronaut.context.annotation.Value;
import jakarta.inject.Singleton;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.net.URI;
import java.util.UUID;

/**
 * 로컬 파일 시스템에서 파일을 읽어오는 구현체.
 */
@Requires(property = "app.storage.type", value = "local")
@Singleton
public class LocalDownloadClient implements DownloadClient {
    @Value("${app.storage.local-path}")
    private String localStoragePath;

    @Override
    public InputStream download(String filename) throws Exception {
        Path path = Path.of(URI.create(String.format("%s/%s", localStoragePath, filename)));
        return Files.newInputStream(path);
    }
}
