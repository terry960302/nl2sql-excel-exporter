package com.pandaterry.application.service.excel;

import com.alibaba.excel.EasyExcel;
import com.pandaterry.application.service.excel.ExcelHierarchyExporter;
import com.pandaterry.application.service.excel.MergeRegionCalculator;
import com.pandaterry.application.vo.CellRange;
import com.pandaterry.application.vo.FlatRow;
import com.pandaterry.application.vo.HierarchyNode;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;

public class ExcelHierarchyExporterTest {

    @Test
    @DisplayName("FlatRow 입력 → 병합 범위 계산 검증 + Excel 파일 생성 확인")
    void export() throws Exception {
        //
        // 1) GIVEN: 샘플 FlatRow 데이터
        //
        List<String> columns = List.of("회사", "부서", "팀", "직원명", "근무일", "장비명");

        FlatRow r1 = new FlatRow();
        r1.getColumns().put("회사", "회사A");
        r1.getColumns().put("부서", "기획부");
        r1.getColumns().put("팀", "A팀");
        r1.getColumns().put("직원명", "홍길동");
        r1.getColumns().put("근무일", "2024-01-01");
        r1.getColumns().put("장비명", "노트북");

        FlatRow r2 = new FlatRow();
        r2.getColumns().put("회사", "회사A");
        r2.getColumns().put("부서", "기획부");
        r2.getColumns().put("팀", "A팀");
        r2.getColumns().put("직원명", "홍길동");
        r2.getColumns().put("근무일", "2024-01-01");
        r2.getColumns().put("장비명", "모니터");

        FlatRow r3 = new FlatRow();
        r3.getColumns().put("회사", "회사A");
        r3.getColumns().put("부서", "기획부");
        r3.getColumns().put("팀", "A팀");
        r3.getColumns().put("직원명", "김영희");
        r3.getColumns().put("근무일", "2024-01-01");
        r3.getColumns().put("장비명", "노트북");

        FlatRow r4 = new FlatRow();
        r4.getColumns().put("회사", "회사A");
        r4.getColumns().put("부서", "개발부");
        r4.getColumns().put("팀", "B팀");
        r4.getColumns().put("직원명", "이수현");
        r4.getColumns().put("근무일", "2024-01-02");
        r4.getColumns().put("장비명", "태블릿");

        List<FlatRow> flatRows = List.of(r1, r2, r3, r4);

        //
        // 2) EXPECTED MERGE RANGE (ASCII 아트)
        //
        //  ┌──────────┬──────────┬──────────┬──────────┬───────────────┬──────────┐
        //  │   회사   │   부서   │   팀     │  직원명  │     근무일    │  장비명   │  ← 1행: 헤더
        //  ├──────────┼──────────┼──────────┼──────────┼───────────────┼──────────┤
        //  │  회사A   │  기획부  │   A팀   │  홍길동  │  2024-01-01   │  노트북   │  ← 2행
        //  │   ────   │   ────   │   ────  │   ────   │      ────      │  모니터   │  ← 3행
        //  │   ────   │   ────   │   ────  │  김영희 │      ────      │  노트북   │  ← 4행
        //  │   ────   │  개발부  │   B팀   │  이수현 │  2024-01-02   │  태블릿   │  ← 5행
        //  └──────────┴──────────┴──────────┴──────────┴───────────────┴──────────┘
        //
        //  - "회사"(0)   : 시트 행 2~5 → CellRange(2,5)
        //  - "부서"(1)   : 시트 행 2~4 → CellRange(2,4)
        //  - "팀"(2)     : 시트 행 2~4 → CellRange(2,4)
        //  - "직원명"(3) : 시트 행 2~3 → CellRange(2,3)
        //  - "근무일"(4) : 시트 행 2~4 → CellRange(2,4)
        //  - "장비명"(5) : 병합 대상 없음

        //
        // 3) MergeRegionCalculator 결과 검증
        //
        MergeRegionCalculator calc = new MergeRegionCalculator();
        Map<Integer, List<CellRange>> mergeMap = calc.calculateMergeRegions(flatRows, columns);

        //   3-1) "회사"(0) → [CellRange(2,5)] 하나만
        assertThat(mergeMap).containsKey(0);
        List<CellRange> compRanges = mergeMap.get(0);
        assertThat(compRanges).hasSize(1);
        assertThat(compRanges.get(0).start()).isEqualTo(2);
        assertThat(compRanges.get(0).end()).isEqualTo(5);

        //   3-2) "부서"(1) → [CellRange(2,4)]
        assertThat(mergeMap).containsKey(1);
        List<CellRange> deptRanges = mergeMap.get(1);
        assertThat(deptRanges).hasSize(1);
        assertThat(deptRanges.get(0).start()).isEqualTo(2);
        assertThat(deptRanges.get(0).end()).isEqualTo(4);

        //   3-3) "팀"(2) → [CellRange(2,4)]
        assertThat(mergeMap).containsKey(2);
        List<CellRange> teamRanges = mergeMap.get(2);
        assertThat(teamRanges).hasSize(1);
        assertThat(teamRanges.get(0).start()).isEqualTo(2);
        assertThat(teamRanges.get(0).end()).isEqualTo(4);

        //   3-4) "직원명"(3) → [CellRange(2,3)]
        assertThat(mergeMap).containsKey(3);
        List<CellRange> nameRanges = mergeMap.get(3);
        assertThat(nameRanges).hasSize(1);
        assertThat(nameRanges.get(0).start()).isEqualTo(2);
        assertThat(nameRanges.get(0).end()).isEqualTo(3);

        //   3-5) "근무일"(4) → [CellRange(2,4)]
        assertThat(mergeMap).containsKey(4);
        List<CellRange> dateRanges = mergeMap.get(4);
        assertThat(dateRanges).hasSize(1);
        assertThat(dateRanges.get(0).start()).isEqualTo(2);
        assertThat(dateRanges.get(0).end()).isEqualTo(4);

        //   3-6) "장비명"(5) → 병합 대상 없음
        assertThat(mergeMap.getOrDefault(5, Collections.emptyList())).isEmpty();

        //
        // 4) EasyExcel을 사용해 실제 엑셀 파일로 저장
        //
        Path outputDir = Paths.get("src/test/resources/output");
        Files.createDirectories(outputDir);
        Path outputFile = outputDir.resolve("test_hierarchy.xlsx");

        try (OutputStream os = Files.newOutputStream(outputFile)) {
            ExcelHierarchyExporter exporter = new ExcelHierarchyExporter();
            exporter.export(flatRows, columns, os);
        }

        // 5) 파일이 정상 생성됐는지 확인
        assertThat(Files.exists(outputFile)).isTrue();
        assertThat(Files.size(outputFile)).isGreaterThan(0);

        //
        // 6) (추가 검증) POI로 병합 영역 직접 읽어보기
        //
        try (Workbook wb = WorkbookFactory.create(Files.newInputStream(outputFile))) {
            Sheet sheet = wb.getSheetAt(0);

            // 엑셀 상에서 병합된 셀 정보를 가져온 뒤, 문자열로 변환해 비교
            // 예: "A2:A5" 같은 형태
            List<String> mergedStrs = new ArrayList<>();
            for (int i = 0; i < sheet.getNumMergedRegions(); i++) {
                CellRangeAddress addr = sheet.getMergedRegion(i);
                mergedStrs.add(addr.formatAsString());
            }
            // 병합 영역은 아래 4가지가 있어야 함
            //   - A2:A5 (회사)
            //   - B2:B4 (부서)
            //   - C2:C4 (팀)
            //   - D2:D3 (직원명)
            //   - E2:E4 (근무일)
            // ※ "장비명"은 병합 없음
            assertThat(mergedStrs).containsExactlyInAnyOrder(
                    "A2:A5",
                    "B2:B4",
                    "C2:C4",
                    "D2:D3",
                    "E2:E4"
            );
        }

        System.out.println("✅ 엑셀 파일 생성 및 병합 영역 검증 완료: " + outputFile.toAbsolutePath());
    }
}
