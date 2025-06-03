package com.pandaterry.application.vo;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * 계층 트리의 한 노드. 각 노드는 name(컬럼명)과 value(셀 내용)를 가지며,
 * children에 하위 노드를 추가할 수 있다. leaf 노드일 때 initLeaf()를 호출하면 rowspan을 1로 설정한다.
 * 마지막에는 accumulateRowspan()를 호출해 자식들의 rowspan 합을 부모로 올려준다.
 */
public class HierarchyNode {
    private final String name;                 // 컬럼명 또는 트리 단계 이름 (예: "회사", "부서", ...)
    private final Object value;                // 셀에 들어갈 실제 값 (예: "회사A", "기획부", ...)
    private final List<HierarchyNode> children = new ArrayList<>();
    private int rowspan;                       // 이 노드가 차지하는 총 행 수 (leaf=1, 부모는 자식 합)

    public HierarchyNode(String name, Object value) {
        this.name = name;
        this.value = value;
        this.rowspan = 0;                      // 기본값 0 (실제 leaf일 때 initLeaf()로 1 세팅)
    }

    public String getName() {
        return name;
    }

    public Object getValue() {
        return value;
    }

    public List<HierarchyNode> getChildren() {
        return children;
    }

    public boolean isLeaf() {
        return children.isEmpty();
    }

    /** leaf 노드일 때 반드시 호출해서 rowspan을 1로 초기화해야 한다. */
    public void initLeaf() {
        this.rowspan = 1;
    }

    /** 자식들의 rowspan 합을 구해 이 노드의 rowspan으로 설정한다. */
    public void setRowspan(int rowspan) {
        this.rowspan = rowspan;
    }

    public int getRowspan() {
        return rowspan;
    }

    public void addChild(HierarchyNode child) {
        this.children.add(child);
    }

    @Override
    public String toString() {
        return "HierarchyNode{" +
                "name='" + name + '\'' +
                ", value=" + value +
                ", rowspan=" + rowspan +
                ", children=" + children.size() +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof HierarchyNode)) return false;
        HierarchyNode that = (HierarchyNode) o;
        return Objects.equals(name, that.name) &&
                Objects.equals(value, that.value) &&
                Objects.equals(children, that.children);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, value, children);
    }
}
