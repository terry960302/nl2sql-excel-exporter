package com.pandaterry.application.service.database;


import java.util.List;

import jakarta.inject.Singleton;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.select.FromItem;
import net.sf.jsqlparser.statement.select.Join;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.select.Select;
import net.sf.jsqlparser.statement.select.SelectBody;
import net.sf.jsqlparser.expression.operators.relational.EqualsTo;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.schema.Table;

import java.util.*;

@Singleton
class JoinKeyExtractor {
    
    /**
     * SQL 쿼리에서 조인 키 정보를 추출합니다.
     * @param sql SQL 쿼리 문자열
     * @return Map<테이블명, Map<컬럼명, 조인키>> 형태로 조인 키 정보 반환
     */
    public Map<String, Map<String, String>> extractJoinKeys(String sql) {
        try {
            Map<String, Map<String, String>> tableJoinKeys = new HashMap<>();
            Statement stmt = CCJSqlParserUtil.parse(sql);
            
            if (stmt instanceof Select) {
                Select select = (Select) stmt;
                SelectBody selectBody = select.getSelectBody();
                
                if (selectBody instanceof PlainSelect) {
                    PlainSelect plainSelect = (PlainSelect) selectBody;
                    
                    // 1. FROM 절의 메인 테이블 처리
                    FromItem fromItem = plainSelect.getFromItem();
                    if (fromItem instanceof Table) {
                        Table mainTable = (Table) fromItem;
                        processTable(mainTable, tableJoinKeys);
                    }
                    
                    // 2. JOIN 절 처리
                    List<Join> joins = plainSelect.getJoins();
                    if (joins != null) {
                        for (Join join : joins) {
                            FromItem rightItem = join.getRightItem();
                            if (rightItem instanceof Table) {
                                Table joinTable = (Table) rightItem;
                                processTable(joinTable, tableJoinKeys);
                                
                                // ON 절에서 조인 키 추출
                                Expression onExpr = join.getOnExpression();
                                if (onExpr instanceof EqualsTo) {
                                    EqualsTo equalsTo = (EqualsTo) onExpr;
                                    processJoinCondition(equalsTo, tableJoinKeys);
                                }
                            }
                        }
                    }
                }
            }
    
            return tableJoinKeys;
        } catch (Exception e) {
            throw new RuntimeException("SQL 파싱 중 오류 발생", e);
        }
    }

    // 테이블 정보를 Map에 추가 (테이블 alias도 함께 저장)
    private void processTable(Table table, Map<String, Map<String, String>> tableJoinKeys) {
        String tableName = table.getName();
        String alias = table.getAlias() != null ? table.getAlias().getName() : null;
        tableJoinKeys.putIfAbsent(tableName, new HashMap<>());
        if (alias != null) {
            tableJoinKeys.putIfAbsent(alias, new HashMap<>());
        }
    }

    // ON 조건에서 조인 키 추출 (예: A.id = B.user_id)
    private void processJoinCondition(EqualsTo equalsTo, Map<String, Map<String, String>> tableJoinKeys) {
        if (equalsTo.getLeftExpression() instanceof Column && equalsTo.getRightExpression() instanceof Column) {
            Column left = (Column) equalsTo.getLeftExpression();
            Column right = (Column) equalsTo.getRightExpression();

            String leftTable = left.getTable() != null ? left.getTable().getName() : null;
            String rightTable = right.getTable() != null ? right.getTable().getName() : null;

            String leftCol = left.getColumnName();
            String rightCol = right.getColumnName();

            if (leftTable != null) {
                tableJoinKeys.get(leftTable).put(leftCol, leftTable + "." + leftCol);
            }
            if (rightTable != null) {
                tableJoinKeys.get(rightTable).put(rightCol, rightTable + "." + rightCol);
            }
        }
    }
}