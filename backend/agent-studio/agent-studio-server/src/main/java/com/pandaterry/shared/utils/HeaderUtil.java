package com.pandaterry.shared.utils;

import io.micronaut.http.HttpHeaders;
import io.micronaut.http.HttpRequest;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HeaderUtil {

    public static Map<String, String> extractSingleValueHeaders(HttpHeaders headers) {
        Map<String, String> headerMap = new HashMap<>();
        for (String name : headers.names()) {
            List<String> values = headers.getAll(name);
            if (!values.isEmpty()) {
                headerMap.put(name, values.get(0)); // 첫 번째 값만 사용
            }
        }
        return headerMap;
    }

    public static Map<String, List<String>> from(HttpRequest request){
        return request.getHeaders().asMap();
    }
}
