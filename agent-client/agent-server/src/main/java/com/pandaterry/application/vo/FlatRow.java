package com.pandaterry.application.vo;

import java.util.LinkedHashMap;
import java.util.Map;

public class FlatRow {
    private final Map<String, Object> columns;
    private final Map<String, Object> joinKeys; // 조인 키 정보 추가

    public FlatRow() {
        this.columns = new LinkedHashMap<>();
        this.joinKeys = new LinkedHashMap<>();
    }

    public void put(String path, Object value) {
        columns.put(path, value);
    }

    public void putJoinKey(String key, Object value) {
        joinKeys.put(key, value);
    }

    public Map<String, Object> getColumns() {
        return columns;
    }

    public Map<String, Object> getJoinKeys() {
        return joinKeys;
    }
}