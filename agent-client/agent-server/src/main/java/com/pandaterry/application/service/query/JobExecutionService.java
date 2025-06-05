package com.pandaterry.application.service.query;

import com.pandaterry.application.service.excel.ExcelHierarchyExporter;
import com.pandaterry.application.vo.FlatRow;
import com.pandaterry.infrastructure.client.UploadClient;
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

    private final SimpleSqlExecutor sqlExecutor;
    private final ExcelHierarchyExporter excelExporter;
    private final UploadClient uploadClient;

    @Inject
    public JobExecutionService(SimpleSqlExecutor sqlExecutor,
                               ExcelHierarchyExporter excelExporter,
                               UploadClient uploadClient) {
        this.sqlExecutor = sqlExecutor;
        this.excelExporter = excelExporter;
        this.uploadClient = uploadClient;
    }

    /**
     * SQL을 실행하고 결과 엑셀 파일을 업로드한다.
     * 결과가 없으면 null을 반환한다.
     */
    public String execute(UUID datasourceId, String sql, UUID jobId) throws Exception {
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
        return uploadClient.upload(temp);
    }
}
