package com.pandaterry.infrastructure.client;

import io.micronaut.context.annotation.Requires;
import io.micronaut.context.annotation.Value;
import jakarta.inject.Singleton;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

/**
 * 로컬 파일 시스템에 결과 파일을 저장하는 구현체.
 */
@Requires(property = "storage.type", value = "local")
@Singleton
public class LocalUploadClient implements UploadClient {

    private final String localDir;

    public LocalUploadClient(@Value("${storage.local-path}") String localDir) {
        this.localDir = localDir;
    }

    @Override
    public String upload(Path filePath) throws Exception {
        Path dir = Paths.get(localDir);
        Files.createDirectories(dir);
        Path dest = dir.resolve(filePath.getFileName());
        Files.move(filePath, dest, StandardCopyOption.REPLACE_EXISTING);
        return dest.toUri().toString();
    }
}
