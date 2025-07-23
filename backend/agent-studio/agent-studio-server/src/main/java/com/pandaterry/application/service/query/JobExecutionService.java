package com.pandaterry.application.service.query;

import com.pandaterry.application.exception.AgentException;
import com.pandaterry.application.service.excel.ExcelHierarchyExporter;
import com.pandaterry.application.vo.ExcelResult;
import com.pandaterry.application.vo.FlatRow;
import com.pandaterry.domain.enums.ErrorCode;
import com.pandaterry.infrastructure.client.UploadClient;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Singleton
public class JobExecutionService {
    private static final String FILENAME_PREFIX = "query-result-";
    private static final String EXCEL_SUFFIX = ".xlsx";

    @Inject
    private SimpleSqlExecutor sqlExecutor;
    @Inject
    private ExcelHierarchyExporter excelExporter;
    @Inject
    private UploadClient uploadClient;

    /**
     * SQL을 실행하고 결과 엑셀 파일을 업로드한다.
     * 결과가 없으면 null을 반환한다.
     */
    public ExcelResult execute(UUID datasourceId, String sql, UUID jobId) throws Exception {
        List<Map<String, Object>> rows = sqlExecutor.execute(datasourceId, sql);
        if (rows.isEmpty()) {
            throw new AgentException(ErrorCode.ROWS_EMPTRY);
        }
        List<String> columnOrder = new ArrayList<>(rows.get(0).keySet());
        List<FlatRow> flatRows = new ArrayList<>();
        fillFlatRows(rows, flatRows, columnOrder);

        Path filePath = writeFile(jobId, flatRows, columnOrder);
        String downloadUrl = uploadClient.upload(filePath);
        return new ExcelResult(filePath.getFileName().toString(), downloadUrl);
    }

    private void fillFlatRows(List<Map<String, Object>> rows, List<FlatRow> flatRows, List<String> columnOrder) {
        for (Map<String, Object> row : rows) {
            FlatRow fr = new FlatRow();
            for (String col : columnOrder) {
                fr.put(col, row.get(col));
            }
            flatRows.add(fr);
        }
    }

    private Path writeFile(UUID jobId, List<FlatRow> flatRows, List<String> columnOrder) {

        try {
            Path temp = Files.createTempFile(FILENAME_PREFIX + jobId, EXCEL_SUFFIX);
            try (OutputStream outputStream = Files.newOutputStream(temp)) {
                excelExporter.export(flatRows, columnOrder, outputStream);
            }
            return temp;
        } catch (IOException e) {
            throw new AgentException(ErrorCode.FILE_TEMP_WRITE_FAILED, e);
        }
    }
}
