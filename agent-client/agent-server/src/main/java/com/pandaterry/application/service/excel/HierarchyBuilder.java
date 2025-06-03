package com.pandaterry.application.service.excel;

import com.pandaterry.application.vo.FlatRow;
import com.pandaterry.application.vo.HierarchyNode;

import java.util.*;

/**
 * 수정된 HierarchyBuilder
 * - 최상위 루트는 new HierarchyNode(null, null) 로 생성
 * - 실제 노드를 생성할 때는 new HierarchyNode(컬럼명, 셀값)
 * - 내부의 children/getValue 메서드는 업로드된 HierarchyNode 정의에 맞춤
 */
public class HierarchyBuilder {

    /**
     * @param columnOrder : ["회사", "부서", "팀", "직원명", "근무일", "장비명"]
     * @param flatRows    : FlatRow 목록
     * @return value=null을 가진 최상위 루트 한 개 → 그 하위에 트리가 붙은 HierarchyNode 반환
     */
    public HierarchyNode build(List<String> columnOrder, List<FlatRow> flatRows) {
        // 1) 최상위 루트 생성 (name=null, value=null)
        HierarchyNode root = new HierarchyNode(null, null);

        // 2) 각 FlatRow마다 트리를 타고 내려가며 노드 생성/재사용
        for (FlatRow row : flatRows) {
            HierarchyNode current = root;

            // 컬럼 순서대로 내려가며
            for (String colName : columnOrder) {
                Object cellValue = row.getColumns().get(colName);

                // 현재 노드의 자식 중, value가 같은 노드가 있으면 그 노드를 reuse
                Optional<HierarchyNode> existing = current.getChildren().stream()
                        .filter(c -> Objects.equals(c.getValue(), cellValue))
                        .findFirst();

                if (existing.isPresent()) {
                    current = existing.get();
                } else {
                    // 없으면 새로 생성해서 붙인다
                    HierarchyNode child = new HierarchyNode(colName, cellValue);
                    current.addChild(child);
                    current = child;
                }
            }
        }

        // 3) 생성된 트리(루트) 반환
        return root;
    }
}
