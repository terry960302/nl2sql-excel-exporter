package com.pandaterry.application.service.excel;

import com.alibaba.excel.EasyExcel;
import com.pandaterry.application.vo.CellRange;
import com.pandaterry.application.vo.FlatRow;
import jakarta.inject.Singleton;

import java.io.OutputStream;
import java.util.*;
import java.util.stream.Collectors;

@Singleton
public class ExcelHierarchyExporter {

        public void export(String sql, List<FlatRow> flatRows, List<String> columnOrder, OutputStream out) {
                // 1) flatRows를 List<List<Object>> 형식으로 변환 (각 FlatRow → columnOrder 순서대로 value만
                // 뽑아낸다)
                List<List<Object>> rowData = flatRows.stream()
                                .map(row -> columnOrder.stream()
                                                .map(colName -> row.getColumns().get(colName))
                                                .collect(Collectors.toList()))
                                .collect(Collectors.toList());

                // 2) 병합 범위 계산 (CellRange(start1Base, end1Base)) 반환
                MergeRegionCalculator calc = new MergeRegionCalculator();
                Map<Integer, List<CellRange>> mergeRegions = calc.calculateMergeRegions(flatRows, columnOrder);

                // 3) EasyExcel로 쓰기
                // - .head(generateHead(columnOrder)) 로 헤더를 작성
                // - .registerWriteHandler(new MergeStrategy(mergeRegions)) 로 병합 전략 등록
                EasyExcel.write(out)
                                .head(generateHead(columnOrder))
                                .registerWriteHandler(new MergeStrategy(mergeRegions))
                                .sheet("Sheet1")
                                .doWrite(rowData);
        }

        /**
         * columnOrder(e.g. ["회사","부서",...])를 EasyExcel 헤더 형식(List<List<String>>)으로 변환
         */
        private List<List<String>> generateHead(List<String> columnOrder) {
                return columnOrder.stream()
                                .map(List::of)
                                .collect(Collectors.toList());
        }
}
