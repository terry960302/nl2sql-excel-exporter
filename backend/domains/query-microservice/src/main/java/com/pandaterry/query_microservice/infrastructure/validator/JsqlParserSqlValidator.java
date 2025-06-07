package com.pandaterry.query_microservice.infrastructure.validator;

import com.pandaterry.query_microservice.domain.enums.ErrorCode;
import com.pandaterry.query_microservice.domain.exception.QueryException;
import com.pandaterry.query_microservice.domain.validator.SqlValidator;
import lombok.RequiredArgsConstructor;
import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.select.*;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.HashSet;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class JsqlParserSqlValidator implements SqlValidator {

    private static final int MAX_SUBQUERY_DEPTH = 2;

    @Override
    public Mono<String> validate(String sql) {
        return Mono.fromCallable(() -> {
            try {
                Statement statement = CCJSqlParserUtil.parse(sql);

                // SELECT-only 검증
                if (!(statement instanceof Select)) {
                    throw new QueryException(ErrorCode.SQL_NOT_SELECT_ONLY);
                }

                Select selectStatement = (Select) statement;
                SelectBody selectBody = selectStatement.getSelectBody();

                // 서브쿼리 depth 검증
                SubqueryDepthVisitor depthVisitor = new SubqueryDepthVisitor();
                selectBody.accept(depthVisitor);

                if (depthVisitor.getMaxDepth() >= MAX_SUBQUERY_DEPTH) {
                    throw new QueryException(ErrorCode.SQL_TOO_COMPLEX);
                }

                // 조인 조건 검증
                JoinConditionVisitor joinVisitor = new JoinConditionVisitor();
                selectBody.accept(joinVisitor);
                if (joinVisitor.hasUnconditionalJoin()) {
                    throw new QueryException(ErrorCode.SQL_JOIN_CONDITION_MISSING);
                }

                // FIXME: 컬럼 별칭 검증은 필수가 아니다.
                // ColumnAliasVisitor aliasVisitor = new ColumnAliasVisitor();
                // selectBody.accept(aliasVisitor);
                // if (aliasVisitor.hasUnnamedColumn()) {
                // throw new QueryException(ErrorCode.SQL_COLUMN_ALIAS_MISSING);
                // }

                return sql;
            } catch (JSQLParserException e) {
                throw new QueryException(ErrorCode.SQL_VALIDATION_FAILED);
            }
        });
    }

    private static class SubqueryDepthVisitor extends SelectVisitorAdapter {
        private int currentDepth = 0;
        private int maxDepth = 0;

        @Override
        public void visit(PlainSelect plainSelect) {
            // 서브쿼리가 있는 경우 처리
            if (plainSelect.getFromItem() instanceof SubSelect) {
                currentDepth++;
                maxDepth = Math.max(maxDepth, currentDepth);
                ((SubSelect) plainSelect.getFromItem()).getSelectBody().accept(this);
                currentDepth--;
            }
        }

        @Override
        public void visit(WithItem withItem) {
            // WITH 절의 서브쿼리 처리
            currentDepth++;
            maxDepth = Math.max(maxDepth, currentDepth);
            withItem.getSubSelect().getSelectBody().accept(this);
            currentDepth--;
        }

        @Override
        public void visit(SetOperationList setOpList) {
            for (SelectBody selectBody : setOpList.getSelects()) {
                currentDepth++;
                maxDepth = Math.max(maxDepth, currentDepth);
                selectBody.accept(this);
                currentDepth--;
            }
        }

        public int getMaxDepth() {
            return maxDepth;
        }
    }

    private static class JoinConditionVisitor extends SelectVisitorAdapter {
        private boolean hasUnconditionalJoin = false;

        @Override
        public void visit(PlainSelect plainSelect) {
            if (plainSelect.getJoins() != null) {
                for (Join join : plainSelect.getJoins()) {
                    if (join.isSimple() || join.getOnExpression() == null) {
                        hasUnconditionalJoin = true;
                        break;
                    }
                }
            }
        }

        @Override
        public void visit(SetOperationList setOpList) {
            for (SelectBody selectBody : setOpList.getSelects()) {
                selectBody.accept(this);
            }
        }

        public boolean hasUnconditionalJoin() {
            return hasUnconditionalJoin;
        }
    }

    private static class ColumnAliasVisitor extends SelectVisitorAdapter {
        private boolean hasUnnamedColumn = false;
        private final Set<String> processedColumns = new HashSet<>();

        @Override
        public void visit(PlainSelect plainSelect) {
            if (plainSelect.getSelectItems() != null) {
                for (SelectItem item : plainSelect.getSelectItems()) {
                    if (item instanceof SelectExpressionItem) {
                        SelectExpressionItem selectExpressionItem = (SelectExpressionItem) item;
                        if (selectExpressionItem.getExpression() instanceof Column) {
                            Column column = (Column) selectExpressionItem.getExpression();
                            String columnName = column.getColumnName();

                            // 이미 처리된 컬럼은 건너뜀
                            if (processedColumns.contains(columnName)) {
                                continue;
                            }
                            processedColumns.add(columnName);

                            // 별칭이 없는 경우
                            if (selectExpressionItem.getAlias() == null) {
                                hasUnnamedColumn = true;
                                break;
                            }
                        }
                    }
                }
            }
        }

        @Override
        public void visit(SetOperationList setOpList) {
            for (SelectBody selectBody : setOpList.getSelects()) {
                selectBody.accept(this);
            }
        }

        public boolean hasUnnamedColumn() {
            return hasUnnamedColumn;
        }
    }
}