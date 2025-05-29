package com.pandaterry.query_microservice.unit.infrastructure.validator;

import com.pandaterry.query_microservice.domain.enums.ErrorCode;
import com.pandaterry.query_microservice.domain.exception.QueryException;
import com.pandaterry.query_microservice.infrastructure.validator.JsqlParserSqlValidator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import reactor.test.StepVerifier;

@DisplayName("JsqlParserSqlValidator 테스트")
class JsqlParserSqlValidatorTest {

    private final JsqlParserSqlValidator validator = new JsqlParserSqlValidator();

    @Nested
    @DisplayName("validate 메서드는")
    class Describe_validate {

        @Test
        @DisplayName("유효한 SELECT 쿼리를 검증한다")
        void validate_유효한SELECT쿼리_성공() {
            // given
            String validSql = "SELECT id, name FROM users WHERE age > 20";

            // when & then
            StepVerifier.create(validator.validate(validSql))
                    .expectNext(validSql)
                    .verifyComplete();
        }

        @Test
        @DisplayName("SELECT가 아닌 쿼리는 거부한다")
        void validate_INSERT쿼리_실패() {
            // given
            String invalidSql = "INSERT INTO users (name) VALUES ('test')";

            // when & then
            StepVerifier.create(validator.validate(invalidSql))
                    .expectErrorMatches(e -> e instanceof QueryException &&
                            ((QueryException) e).getErrorCode() == ErrorCode.SQL_NOT_SELECT_ONLY)
                    .verify();
        }

        @Test
        @DisplayName("문법이 잘못된 SQL은 거부한다")
        void validate_잘못된문법_실패() {
            // given
            String invalidSql = "SELECT * FROM users WHERE";

            // when & then
            StepVerifier.create(validator.validate(invalidSql))
                    .expectErrorMatches(e -> e instanceof QueryException &&
                            ((QueryException) e).getErrorCode() == ErrorCode.SQL_VALIDATION_FAILED)
                    .verify();
        }

        @Test
        @DisplayName("서브쿼리 깊이가 제한을 초과하면 거부한다")
        void validate_서브쿼리깊이초과_실패() {
            // given
            String invalidSql = "SELECT * FROM (SELECT * FROM (SELECT * FROM users))";

            // when & then
            StepVerifier.create(validator.validate(invalidSql))
                    .expectErrorMatches(e -> e instanceof QueryException &&
                            ((QueryException) e).getErrorCode() == ErrorCode.SQL_TOO_COMPLEX)
                    .verify();
        }

        @Test
        @DisplayName("조인 조건이 없는 쿼리는 거부한다")
        void validate_조인조건없음_실패() {
            // given
            String invalidSql = "SELECT u.id, o.id FROM users u, orders o";

            // when & then
            StepVerifier.create(validator.validate(invalidSql))
                    .expectErrorMatches(e -> e instanceof QueryException &&
                            ((QueryException) e).getErrorCode() == ErrorCode.SQL_JOIN_CONDITION_MISSING)
                    .verify();
        }

        @Test
        @DisplayName("조인 조건과 컬럼 별칭이 모두 있는 유효한 쿼리를 검증한다")
        void validate_유효한조인쿼리_성공() {
            // given
            String validSql = "SELECT u.id as user_id, o.id as order_id " +
                    "FROM users u " +
                    "JOIN orders o ON u.id = o.user_id";

            // when & then
            StepVerifier.create(validator.validate(validSql))
                    .expectNext(validSql)
                    .verifyComplete();
        }
    }
}