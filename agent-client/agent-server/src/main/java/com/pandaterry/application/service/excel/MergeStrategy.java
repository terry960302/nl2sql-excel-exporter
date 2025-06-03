package com.pandaterry.application.service.excel;

import com.alibaba.excel.metadata.Head;
import com.alibaba.excel.write.handler.context.CellWriteHandlerContext;
import com.alibaba.excel.write.merge.AbstractMergeStrategy;
import com.pandaterry.application.vo.CellRange;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.util.CellRangeAddress;

import java.util.List;
import java.util.Map;

public class MergeStrategy extends AbstractMergeStrategy {
    // key: 0-base 컬럼 인덱스, value: 병합할 행 범위(시트 1-base 번호)
    private final Map<Integer, List<CellRange>> mergeRegions;

    public MergeStrategy(Map<Integer, List<CellRange>> mergeRegions) {
        this.mergeRegions = mergeRegions;
    }

    @Override
    public void afterCellDispose(CellWriteHandlerContext context) {
        // 헤더(Head)가 쓰이는 셀은 pass
        if (context.getHead()) {
            return;
        }
        // 데이터 셀이 쓰인 이후에 실제 병합 로직 호출
        merge(
                context.getWriteSheetHolder().getSheet(),
                context.getCell(),
                context.getHeadData(),         // 헤더 정보 (사용하지 않음)
                context.getRelativeRowIndex()  // 데이터가 몇 번째 쓰였는지 (0-base)
        );
    }

    /**
     * @param sheet            POI Sheet 객체
     * @param cell             현재 그려진 Cell (0-base row/col 로 정보 조회 가능)
     * @param head             헤더 객체 (사용하지 않음)
     * @param relativeRowIndex “이 셀”이 데이터에서 몇 번째 행인지(0-base) 저장. (헤더 제외)
     */
    @Override
    protected void merge(Sheet sheet, Cell cell, Head head, Integer relativeRowIndex) {
        // 1) 병합 정보가 없으면 종료
        if (mergeRegions == null || mergeRegions.isEmpty()) {
            return;
        }

        // 2) 이 셀이 속한 컬럼 인덱스를 구한다 (0-base)
        int colIndex = cell.getColumnIndex();

        // 3) 이 컬럼에 병합 범위가 미리 계산돼 있다면 검사
        List<CellRange> rangesForThisCol = mergeRegions.get(colIndex);
        if (rangesForThisCol == null || rangesForThisCol.isEmpty()) {
            return;
        }

        // 4) 이 셀의 실제 “Excel 행 번호(1-base)” 계산
        //    → cell.getRowIndex()는 0-base이므로 +1 해야 1-base가 된다.
        int excelRowIndex1Base = cell.getRowIndex() + 1;

        // 5) 병합 목록 중 “start”가 이 행과 일치하는 범위를 찾아 병합 시작
        for (CellRange r : rangesForThisCol) {
            if (r.start() == excelRowIndex1Base) {
                // POI CellRangeAddress 의 생성자는 모두 0-base이므로
                //  start-1, end-1 로 변환한다.
                int firstRow0Base = r.start() - 1;
                int lastRow0Base = r.end() - 1;

                sheet.addMergedRegionUnsafe(
                        new CellRangeAddress(
                                firstRow0Base,
                                lastRow0Base,
                                colIndex,
                                colIndex
                        )
                );
                // 한 컬럼 당 겹치는 병합 구간은 중복되지 않으므로,
                // 찾으면 바로 break 처리
                break;
            }
        }
    }
}
