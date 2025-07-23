package com.pandaterry.infrastructure;

import java.util.List;
import java.util.Map;

public class RequestHeaderHolder {
    private static final InheritableThreadLocal<Map<String, List<String>>> headers = new InheritableThreadLocal<>();

    public static void set(Map<String, List<String>> headerMap) {
        headers.set(headerMap);
    }

    public static Map<String, List<String>> get() {
        return headers.get();
    }

    public static void clear() {
        headers.remove();
    }
}