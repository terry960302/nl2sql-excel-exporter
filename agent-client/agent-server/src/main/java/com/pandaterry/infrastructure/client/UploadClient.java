package com.pandaterry.infrastructure.client;

import java.nio.file.Path;

/**
 * 파일 업로드를 담당하는 클라이언트 인터페이스.
 */
public interface UploadClient {
    /**
     * 지정한 파일을 업로드 후 다운로드 URL을 반환한다.
     */
    String upload(Path filePath) throws Exception;
}
