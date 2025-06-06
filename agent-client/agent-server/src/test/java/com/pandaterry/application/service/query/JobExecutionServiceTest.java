package com.pandaterry.application.service.query;

import com.pandaterry.application.service.excel.ExcelHierarchyExporter;
import com.pandaterry.application.vo.ExcelResult;
import com.pandaterry.infrastructure.client.UploadClient;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class JobExecutionServiceTest {

    @Test
    void execute_업로드호출() throws Exception {
        SimpleSqlExecutor sqlExecutor = mock(SimpleSqlExecutor.class);
        ExcelHierarchyExporter exporter = mock(ExcelHierarchyExporter.class);
        UploadClient uploadClient = mock(UploadClient.class);

        JobExecutionService service = new JobExecutionService(sqlExecutor, exporter, uploadClient);

        UUID dsId = UUID.randomUUID();
        UUID jobId = UUID.randomUUID();

        when(sqlExecutor.execute(dsId, "select 1"))
                .thenReturn(List.of(Map.of("a", 1)));
        when(uploadClient.upload(any(Path.class))).thenReturn("http://download");

        ExcelResult result = service.execute(dsId, "select 1", jobId);

        verify(sqlExecutor).execute(dsId, "select 1");
        verify(exporter).export(any(), any(), any());
        verify(uploadClient).upload(any(Path.class));
        assertThat(result.downloadUrl()).isEqualTo("http://download");
    }
}
