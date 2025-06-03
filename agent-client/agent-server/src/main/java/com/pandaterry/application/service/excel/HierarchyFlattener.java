package com.pandaterry.application.service.excel;

import com.pandaterry.application.vo.HierarchyNode;

import java.util.*;

public class HierarchyFlattener {

    /**
     * @param root 루트 노드 (value=null)
     * @return 2차원 데이터: [ [회사, 부서, 팀, 직원명, 근무일, 장비명], ... ] 목록
     */
    public List<List<Object>> flatten(HierarchyNode root) {
        List<List<Object>> output = new ArrayList<>();
        // 루트 바로 아래 자식(회사단)부터 DFS
        for (HierarchyNode company : root.getChildren()) {
            dfs(company, new ArrayList<>(), output);
        }
        return output;
    }

    private void dfs(HierarchyNode node, List<Object> path, List<List<Object>> output) {
        path.add(node.getValue());

        if (node.getChildren().isEmpty()) {
            // leaf까지 내려왔다면 path 전체가 한 줄이 된다.
            output.add(new ArrayList<>(path));
        } else {
            // 중간 노드: 모든 자식에 대해 다시 내려간다.
            for (HierarchyNode c : node.getChildren()) {
                dfs(c, path, output);
                // 하나의 branch가 끝나면, 마지막으로 넣은 값을 제거
                path.remove(path.size() - 1);
            }
        }
    }
}
