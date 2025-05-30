package com.pandaterry.query_microservice.unit.infrastructure.validator;

import com.pandaterry.query_microservice.domain.enums.ErrorCode;
import com.pandaterry.query_microservice.domain.exception.QueryException;
import com.pandaterry.query_microservice.infrastructure.validator.JsqlParserValidator;

import reactor.test.StepVerifier;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

class JsqlParserValidatorTest {

    private JsqlParserValidator validator;

    @BeforeEach
    void setUp() {
        validator = new JsqlParserValidator();
    }

    @Test
    void validate_WithValidSelect_ShouldNotThrowException() {
        String validSql = "SELECT id, name FROM users WHERE age > 18";
        StepVerifier.create(validator.validate(validSql))
                .expectNext(validSql)
                .verifyComplete();
    }

    @Test
    void validate_WithInsertStatement_ShouldThrowException() {
        String insertSql = "INSERT INTO users (id, name) VALUES (1, 'test')";
        StepVerifier.create(validator.validate(insertSql))
                .expectErrorMatches(throwable -> throwable instanceof QueryException &&
                        ((QueryException) throwable).getErrorCode() == ErrorCode.SQL_NOT_SELECT_ONLY)
                .verify();
    }

    @Test
    void validate_WithWildcard_ShouldThrowException() {
        String wildcardSql = "SELECT * FROM users";

        StepVerifier.create(validator.validate(wildcardSql))
                .expectErrorMatches(throwable -> throwable instanceof QueryException &&
                        ((QueryException) throwable).getErrorCode() == ErrorCode.SQL_WILDCARD_NOT_ALLOWED)
                .verify();
    }

    @Test
    void validate_WithDeepSubquery_ShouldThrowException() {
        String deepSubquerySql = "SELECT id FROM (SELECT id FROM (SELECT id FROM users))";
        StepVerifier.create(validator.validate(deepSubquerySql))
                .expectErrorMatches(throwable -> throwable instanceof QueryException &&
                        ((QueryException) throwable).getErrorCode().equals(ErrorCode.SQL_SUBQUERY_DEPTH_EXCEEDED))
                .verify();
    }

    @Test
    void validate_WithMissingJoinCondition_ShouldThrowException() {
        String joinSql = "SELECT a.id, b.name FROM table1 a JOIN table2 b";
        StepVerifier.create(validator.validate(joinSql))
                .expectErrorMatches(throwable -> throwable instanceof QueryException &&
                        ((QueryException) throwable).getErrorCode() == ErrorCode.SQL_JOIN_CONDITION_MISSING)
                .verify();
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "SELECT id FROM users WHERE id IN (SELECT id FROM users)",
            "SELECT id FROM users WHERE EXISTS (SELECT 1 FROM users)",
            "SELECT id FROM (SELECT id FROM users) a"
    })
    void validate_WithValidSubqueries_ShouldNotThrowException(String sql) {
        StepVerifier.create(validator.validate(sql))
                .expectNext(sql)
                .verifyComplete();
    }

    @Test
    void validate_WithValidJoin_ShouldNotThrowException() {
        String joinSql = "SELECT a.id, b.name FROM table1 a JOIN table2 b ON a.id = b.id";
        StepVerifier.create(validator.validate(joinSql))
                .expectNext(joinSql)
                .verifyComplete();
    }

    @Test
    void validate_WithInvalidSyntax_ShouldThrowException() {
        String invalidSql = "SELECT FROM users";
        StepVerifier.create(validator.validate(invalidSql))
                .expectErrorMatches(throwable -> throwable instanceof QueryException &&
                        ((QueryException) throwable).getErrorCode() == ErrorCode.SQL_VALIDATION_FAILED)
                .verify();
    }
}