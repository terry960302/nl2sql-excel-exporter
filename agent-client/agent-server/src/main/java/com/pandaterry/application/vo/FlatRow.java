package com.pandaterry.application.vo;

import java.util.LinkedHashMap;
import java.util.Map;

public class FlatRow {
    private final Map<String, Object> columns;

    public FlatRow() {
        this.columns = new LinkedHashMap<>();
    }

    public void put(String path, Object value) {
        columns.put(path, value);
    }

    public Map<String, Object> getColumns() {
        return columns;
    }
}