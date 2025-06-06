package com.pandaterry.infrastructure.client;

import org.junit.jupiter.api.Test;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;

class LocalDownloadClientTest {

    private final LocalDownloadClient client = new LocalDownloadClient();

    @Test
    void fileSchemeDownload() throws Exception {
        Path temp = Files.createTempFile("sample", ".txt");
        Files.writeString(temp, "hello", StandardCharsets.UTF_8);

        try (InputStream is = client.download(temp.toUri().toString())) {
            String result = new String(is.readAllBytes(), StandardCharsets.UTF_8);
            assertThat(result).isEqualTo("hello");
        }
    }
}
