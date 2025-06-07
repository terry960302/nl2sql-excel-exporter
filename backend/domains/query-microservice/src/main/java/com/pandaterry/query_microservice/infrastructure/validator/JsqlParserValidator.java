package com.pandaterry.query_microservice.infrastructure.validator;

import com.pandaterry.query_microservice.domain.enums.ErrorCode;
import com.pandaterry.query_microservice.domain.exception.QueryException;
import com.pandaterry.query_microservice.domain.validator.SqlValidator;
import lombok.RequiredArgsConstructor;
import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.select.*;
import reactor.core.publisher.Mono;

import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class JsqlParserValidator implements SqlValidator {
    private static final int MAX_SUBQUERY_DEPTH = 2;

    @Override
    public Mono<String> validate(String sql) {
        return Mono.fromCallable(() -> {
            try {
                Statement statement = CCJSqlParserUtil.parse(sql);
                if (!(statement instanceof Select)) {
                    throw new QueryException(ErrorCode.SQL_NOT_SELECT_ONLY);
                }

                Select selectStatement = (Select) statement;
                SelectBody selectBody = selectStatement.getSelectBody();

                int currentDepth = calculateQueryDepth(selectBody, 0);

                validateSelectBody(selectBody, currentDepth);
                return sql;
            } catch (JSQLParserException e) {
                throw new QueryException(ErrorCode.SQL_VALIDATION_FAILED);
            }
        });
    }

    private void validateSelectBody(SelectBody selectBody, int depth) {
        if (selectBody instanceof PlainSelect) {
            validatePlainSelect((PlainSelect) selectBody, depth);
        } else if (selectBody instanceof SetOperationList) {
            validateSetOperationList((SetOperationList) selectBody, depth);
        }
    }

    private void validatePlainSelect(PlainSelect plainSelect, int depth) {
        // 와일드카드(*) 검사
        if (plainSelect.getSelectItems().stream()
                .anyMatch(item -> item.toString().equals("*"))) {
            throw new QueryException(ErrorCode.SQL_WILDCARD_NOT_ALLOWED);
        }

        // 서브쿼리 깊이 검사
        if (depth > MAX_SUBQUERY_DEPTH) {
            throw new QueryException(ErrorCode.SQL_SUBQUERY_DEPTH_EXCEEDED);
        }

        // 서브쿼리 검사
        if (plainSelect.getFromItem() instanceof SubSelect) {
            validateSelectBody(((SubSelect) plainSelect.getFromItem()).getSelectBody(), depth + 1);
        }

        // WHERE 절의 서브쿼리 검사
        if (plainSelect.getWhere() != null) {
            validateWhereClause(plainSelect.getWhere(), depth);
        }

        // JOIN 조건 검사
        if (plainSelect.getJoins() != null) {
            validateJoins(plainSelect.getJoins(), depth);
        }
    }

    private void validateSetOperationList(SetOperationList setOpList, int depth) {
        for (SelectBody selectBody : setOpList.getSelects()) {
            validateSelectBody(selectBody, depth);
        }
    }

    private void validateWhereClause(net.sf.jsqlparser.expression.Expression where, int depth) {
        if (where instanceof net.sf.jsqlparser.expression.operators.relational.InExpression) {
            net.sf.jsqlparser.expression.operators.relational.InExpression inExpr = (net.sf.jsqlparser.expression.operators.relational.InExpression) where;
            if (inExpr.getRightExpression() instanceof SubSelect) {
                validateSelectBody(((SubSelect) inExpr.getRightExpression()).getSelectBody(), depth + 1);
            }
        }
    }

    private void validateJoins(java.util.List<Join> joins, int depth) {
        Set<String> joinConditions = new HashSet<>();

        for (Join join : joins) {
            if (join.getOnExpressions().isEmpty()) {
                throw new QueryException(ErrorCode.SQL_JOIN_CONDITION_MISSING);
            }

            // JOIN 조건 중복 검사
            String condition = join.getOnExpression().toString();
            if (!joinConditions.add(condition)) {
                throw new QueryException(ErrorCode.SQL_JOIN_CONDITION_MISSING);
            }

            // JOIN의 서브쿼리 검사
            if (join.getRightItem() instanceof SubSelect) {
                validateSelectBody(((SubSelect) join.getRightItem()).getSelectBody(), depth + 1);
            }
        }
    }

    private int calculateQueryDepth(SelectBody selectBody, int depth) {
        if (selectBody instanceof PlainSelect) {
            return calculateQueryDepth((PlainSelect) selectBody, depth);
        } else if (selectBody instanceof SetOperationList) {
            return calculateQueryDepth((SetOperationList) selectBody, depth);
        }
        return 0;
    }

    private int calculateQueryDepth(PlainSelect plainSelect, int depth) {
        if (plainSelect.getFromItem() instanceof SubSelect) {
            depth = calculateQueryDepth(((SubSelect) plainSelect.getFromItem()).getSelectBody(), depth + 1);
        }
        return depth;
    }
}