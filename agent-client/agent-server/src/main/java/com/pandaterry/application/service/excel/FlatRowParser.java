package com.pandaterry.application.service.excel;

import com.pandaterry.application.vo.FlatRow;
import jakarta.inject.Singleton;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Singleton
public class FlatRowParser {
    public List<FlatRow> parse(ResultSet rs) throws SQLException {
        List<FlatRow> rows = new ArrayList<>();
        ResultSetMetaData metaData = rs.getMetaData();
        int columnCount = metaData.getColumnCount();

        while (rs.next()) {
            FlatRow row = new FlatRow(); // 순서를 유지
            for (int i = 1; i <= columnCount; i++) {
                String columnLabel = metaData.getColumnLabel(i); // alias 사용
                Object value = rs.getObject(i);
                row.put(columnLabel, value);
            }
            rows.add(row);
        }

        return rows;
    }
}
