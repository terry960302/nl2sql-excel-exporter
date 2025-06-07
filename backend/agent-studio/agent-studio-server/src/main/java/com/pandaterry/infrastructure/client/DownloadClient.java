package com.pandaterry.infrastructure.client;

import java.io.InputStream;

/**
 * 파일 다운로드를 담당하는 클라이언트 인터페이스.
 */
public interface DownloadClient {
    /**
     * 지정한 URL에서 파일을 다운로드한다.
     */
    InputStream download(String url) throws Exception;
}
