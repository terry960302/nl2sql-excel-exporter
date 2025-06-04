package com.pandaterry.infrastructure.client;

import io.micronaut.http.HttpRequest;
import io.micronaut.http.MutableHttpRequest;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.uri.UriBuilder;
import jakarta.annotation.Nullable;

import java.util.Map;

public abstract class BaseServiceClient {

    protected final HttpClient httpClient;
    protected final String baseUrl;
    protected final String agentSecret;

    protected BaseServiceClient(HttpClient httpClient, String baseUrl, String agentSecret) {
        this.httpClient = httpClient;
        this.baseUrl = baseUrl;
        this.agentSecret = agentSecret;


    }

    /**
     * 기본 GET 호출 (추가 헤더 없이)
     */
    protected <T> T get(String path, Class<T> responseType) {
        return get(path, null, responseType);
    }

    /**
     * 추가 헤더를 같이 전달할 수 있는 GET 호출
     *
     * @param path         URI 경로 (baseUrl 뒤에 붙일 부분)
     * @param extraHeaders 추가로 포함할 헤더들 (키: 헤더명, 값: 헤더값), 없으면 null
     * @param responseType 응답을 매핑할 클래스 타입
     */
    protected <T> T get(String path,
                        @Nullable Map<String, String> extraHeaders,
                        Class<T> responseType) {
        // 1) MutableHttpRequest로 선언해야 header() 메서드가 보인다
        MutableHttpRequest<?> request = HttpRequest
                .GET(UriBuilder.of(baseUrl).path(path).build())
                .bearerAuth(agentSecret);

        // 2) extraHeaders가 있다면 반복문으로 헤더 추가
        if (extraHeaders != null) {
            extraHeaders.forEach(request::header);
        }

        // 3) Micronaut HttpClient로 동기 호출
        return httpClient.toBlocking().retrieve(request, responseType);
    }

    /**
     * 기본 POST 호출 (응답 타입 없이, 단순 전송)
     */
    protected void post(String path, Object body) {
        post(path, body, null, null);
    }

    /**
     * 응답을 받을 수 있는 POST 호출 (추가 헤더 없이)
     *
     * @param path         URI 경로
     * @param body         요청 바디 객체 (JSON으로 직렬화됨)
     * @param responseType 반환받을 클래스 타입 (null이면 바디 처리 없이 exchange)
     */
    protected <T> void post(String path, Object body, @Nullable Class<T> responseType) {
        post(path, body, null, responseType);
    }

    /**
     * 추가 헤더를 같이 전달할 수 있는 POST 호출
     *
     * @param path         URI 경로 (baseUrl 뒤에 붙일 부분)
     * @param body         요청 바디 객체 (JSON으로 직렬화됨)
     * @param extraHeaders 추가로 포함할 헤더들 (키: 헤더명, 값: 헤더값), 없으면 null
     * @param responseType 응답을 매핑할 클래스 타입 (null이면 exchange만 수행)
     */
    protected <T> void post(String path,
                            Object body,
                            @Nullable Map<String, String> extraHeaders,
                            @Nullable Class<T> responseType) {
        // 1) MutableHttpRequest로 선언
        MutableHttpRequest<Object> request = HttpRequest
                .POST(UriBuilder.of(baseUrl).path(path).build(), body)
                .bearerAuth(agentSecret);

        // 2) extraHeaders가 있다면 반복문으로 헤더 추가
        if (extraHeaders != null) {
            extraHeaders.forEach(request::header);
        }

        // 3) Micronaut HttpClient 호출
        if (responseType != null) {
            httpClient.toBlocking().retrieve(request, responseType);
        } else {
            httpClient.toBlocking().exchange(request);
        }
    }
}