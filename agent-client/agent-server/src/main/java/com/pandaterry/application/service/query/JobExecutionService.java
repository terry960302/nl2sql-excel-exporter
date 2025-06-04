package com.pandaterry.application.service.query;

import com.pandaterry.application.service.excel.ExcelHierarchyExporter;
import com.pandaterry.application.vo.FlatRow;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;

import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Singleton
public class JobExecutionService {

    @Inject
    private SimpleSqlExecutor sqlExecutor;
    @Inject
    private ExcelHierarchyExporter excelExporter;

    /**
     * SQL을 실행하고 결과를 엑셀 파일로 저장
     * 결과가 없을 경우 Optional.empty()를 반환.
     */
    public Path execute(UUID datasourceId, String sql, UUID jobId) throws Exception {
        List<Map<String, Object>> rows = sqlExecutor.execute(datasourceId, sql);
        if (rows.isEmpty()) {
            return null;
        }
        List<String> columnOrder = new ArrayList<>(rows.get(0).keySet());
        List<FlatRow> flatRows = new ArrayList<>();
        for (Map<String, Object> row : rows) {
            FlatRow fr = new FlatRow();
            for (String col : columnOrder) {
                fr.put(col, row.get(col));
            }
            flatRows.add(fr);
        }
        Path temp = Files.createTempFile("query-result-" + jobId, ".xlsx");
        try (OutputStream os = Files.newOutputStream(temp)) {
            excelExporter.export(flatRows, columnOrder, os);
        }
        return temp;
    }
}
