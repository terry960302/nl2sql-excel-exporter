package com.pandaterry.query_microservice.unit.application.service;

import com.pandaterry.query_microservice.application.service.QueryValidator;
import com.pandaterry.query_microservice.domain.exception.QueryException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class QueryValidatorTest {
    @InjectMocks
    private QueryValidator queryValidator;

    @Test
    void validateNaturalText_성공() {
        // given
        String validText = "이번달에 VIP 고객의 평균 구매 금액을 보여줘";

        // when & then
        assertDoesNotThrow(() -> queryValidator.validateNaturalText(validText));
    }

    @Test
    void validateNaturalText_빈문자열_실패() {
        // given
        String emptyText = "";

        // when & then
        assertThrows(QueryException.class,
                () -> queryValidator.validateNaturalText(emptyText));
    }

    @Test
    void validateNaturalText_길이초과_실패() {
        // given
        String longText = "a".repeat(201);

        // when & then
        assertThrows(QueryException.class,
                () -> queryValidator.validateNaturalText(longText));
    }

    @Test
    void validateNaturalText_금지키워드_실패() {
        // given
        String textWithForbiddenKeyword = "DROP TABLE users";

        // when & then
        assertThrows(QueryException.class,
                () -> queryValidator.validateNaturalText(textWithForbiddenKeyword));
    }
}