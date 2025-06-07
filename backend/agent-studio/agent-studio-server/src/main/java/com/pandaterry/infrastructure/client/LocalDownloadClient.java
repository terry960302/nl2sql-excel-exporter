package com.pandaterry.infrastructure.client;

import io.micronaut.context.annotation.Requires;
import jakarta.inject.Singleton;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.net.URI;

/**
 * 로컬 파일 시스템에서 파일을 읽어오는 구현체.
 */
@Requires(property = "storage.type", value = "local")
@Singleton
public class LocalDownloadClient implements DownloadClient {

    @Override
    public InputStream download(String url) throws Exception {
        Path path = Path.of(URI.create(url));
        return Files.newInputStream(path);
    }
}
