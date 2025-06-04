package com.pandaterry.application.service.excel;

import com.pandaterry.application.service.excel.ExcelHierarchyExporter;
import com.pandaterry.application.service.excel.JoinKeyOrderParser;
import com.pandaterry.application.service.excel.MergeRegionCalculator;
import com.pandaterry.application.vo.CellRange;
import com.pandaterry.application.vo.FlatRow;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.ss.util.CellRangeAddress;
import org.junit.jupiter.api.Test;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.io.OutputStream;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;

public class JoinKeyMergeIntegrationTest {

    @Test
    void joinKeyBasedMerge() throws Exception {
        String sql = "SELECT a.id as \"a.id\", b.id as \"b.id\", c.name FROM a JOIN b ON a.id = b.a_id JOIN c ON b.id = c.b_id";
        JoinKeyOrderParser parser = new JoinKeyOrderParser();
        List<String> joinKeys = parser.parseJoinKeys(sql);

        List<String> columns = List.of("a.id", "b.id", "c.name");

        FlatRow r1 = new FlatRow();
        r1.getColumns().put("a.id", 1);
        r1.getColumns().put("b.id", 10);
        r1.getColumns().put("c.name", "x");

        FlatRow r2 = new FlatRow();
        r2.getColumns().put("a.id", 1);
        r2.getColumns().put("b.id", 10);
        r2.getColumns().put("c.name", "y");

        FlatRow r3 = new FlatRow();
        r3.getColumns().put("a.id", 1);
        r3.getColumns().put("b.id", 20);
        r3.getColumns().put("c.name", "z");

        FlatRow r4 = new FlatRow();
        r4.getColumns().put("a.id", 2);
        r4.getColumns().put("b.id", 30);
        r4.getColumns().put("c.name", "p");

        List<FlatRow> flatRows = List.of(r1, r2, r3, r4);

        MergeRegionCalculator calc = new MergeRegionCalculator();
        Map<Integer, List<CellRange>> mergeMap = calc.calculateMergeRegions(flatRows, columns, joinKeys);
        assertThat(mergeMap.get(0)).containsExactly(new CellRange(2,4));
        assertThat(mergeMap.get(1)).containsExactly(new CellRange(2,3));

        Path outputDir = Paths.get("src/test/resources/output");
        Files.createDirectories(outputDir);
        Path outputFile = outputDir.resolve("join_key_merge.xlsx");

        try (OutputStream os = Files.newOutputStream(outputFile)) {
            ExcelHierarchyExporter exporter = new ExcelHierarchyExporter();
            exporter.export(flatRows, columns, joinKeys, os);
        }

        try (Workbook wb = WorkbookFactory.create(Files.newInputStream(outputFile))) {
            Sheet sheet = wb.getSheetAt(0);
            List<String> merged = new ArrayList<>();
            for (int i = 0; i < sheet.getNumMergedRegions(); i++) {
                CellRangeAddress addr = sheet.getMergedRegion(i);
                merged.add(addr.formatAsString());
            }
            assertThat(merged).containsExactlyInAnyOrder("A2:A4", "B2:B3");
        }
    }
}
