package com.pandaterry.application.service.excel;

import com.pandaterry.application.vo.CellRange;
import com.pandaterry.application.vo.FlatRow;

import java.util.*;

public class MergeRegionCalculator {

    /**
     * @param flatRows    : 실제 데이터 행 개수
     * @param columnOrder : 컬럼 순서(예: ["회사","부서","팀","직원명","근무일","장비명"])
     * @return Map<컬럼 인덱스, List<병합 범위(CellRange)>>
     *         → CellRange 의 start/end는 반드시 "Excel 시트 기준 1‐베이스 행 번호"로 리턴 (헤더 1행 이후 데이터가 2행부터 시작)
     */
    public Map<Integer, List<CellRange>> calculateMergeRegions(
            List<FlatRow> flatRows, List<String> columnOrder) {
        return calculateMergeRegions(flatRows, columnOrder, Collections.emptyList());
    }

    public Map<Integer, List<CellRange>> calculateMergeRegions(
            List<FlatRow> flatRows, List<String> columnOrder, List<String> joinKeyOrder) {
        List<String> finalOrder = buildFinalOrder(columnOrder, joinKeyOrder);

        Map<Integer, List<CellRange>> mergeMap = new HashMap<>();
        int rowCount = flatRows.size();
        for (int col = 0; col < finalOrder.size(); col++) {
            String column = finalOrder.get(col);
            Object prev = null;
            int blockStart = 0;

            for (int i = 0; i < rowCount; i++) {
                Object curr = flatRows.get(i).getColumns().get(column);

                if (!Objects.equals(prev, curr)) {
                    // 이전 블록이 2개 이상 연속이면 병합해야 함
                    if (prev != null && (i - blockStart) >= 2) {
                        // Excel 행 번호로 변환: flatRows idx 0 → 시트 2, idx i-1 → 시트 (i-1)+2
                        int excelStart = blockStart + 2;
                        int excelEnd = (i - 1) + 2;
                        mergeMap.computeIfAbsent(col, k -> new ArrayList<>())
                                .add(new CellRange(excelStart, excelEnd));
                    }
                    prev = curr;
                    blockStart = i;
                }

                // 마지막 행 도달했을 때도 블록 처리
                if (i == rowCount - 1 && (rowCount - blockStart) >= 2) {
                    int excelStart = blockStart + 2;
                    int excelEnd = i + 2;
                    mergeMap.computeIfAbsent(col, k -> new ArrayList<>())
                            .add(new CellRange(excelStart, excelEnd));
                }
            }
        }
        return mergeMap;
    }

    private List<String> buildFinalOrder(List<String> columnOrder, List<String> joinKeyOrder) {
        if (joinKeyOrder == null || joinKeyOrder.isEmpty()) {
            return new ArrayList<>(columnOrder);
        }
        LinkedHashSet<String> set = new LinkedHashSet<>(joinKeyOrder);
        set.addAll(columnOrder);
        return new ArrayList<>(set);
    }
}
