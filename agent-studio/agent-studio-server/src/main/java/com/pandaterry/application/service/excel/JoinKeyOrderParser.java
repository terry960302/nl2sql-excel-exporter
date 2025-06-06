package com.pandaterry.application.service.excel;

import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.operators.relational.EqualsTo;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.select.Join;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.select.Select;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Utility class extracting join key order from a SELECT query using JSQLParser.
 */
public class JoinKeyOrderParser {

    /**
     * Parses given SQL and returns the left column of each JOIN condition in order.
     * When parsing fails or no JOIN clause exists, an empty list is returned.
     */
    public List<String> parseJoinKeys(String sql) {
        try {
            Statement stmt = CCJSqlParserUtil.parse(sql);
            if (!(stmt instanceof Select select)) {
                return Collections.emptyList();
            }
            if (!(select.getSelectBody() instanceof PlainSelect plain)) {
                return Collections.emptyList();
            }
            List<Join> joins = plain.getJoins();
            if (joins == null || joins.isEmpty()) {
                return Collections.emptyList();
            }
            List<String> keys = new ArrayList<>();
            for (Join join : joins) {
                Expression onExpr = join.getOnExpression();
                if (onExpr instanceof EqualsTo eq) {
                    Expression left = eq.getLeftExpression();
                    if (left instanceof Column col) {
                        keys.add(col.getFullyQualifiedName());
                    }
                }
            }
            return keys;
        } catch (JSQLParserException e) {
            return Collections.emptyList();
        }
    }
}
