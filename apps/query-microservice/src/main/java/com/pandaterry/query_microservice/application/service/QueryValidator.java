package com.pandaterry.query_microservice.application.service;

import com.pandaterry.query_microservice.domain.enums.ErrorCode;
import com.pandaterry.query_microservice.domain.exception.QueryException;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class QueryValidator {
    private static final Set<String> FORBIDDEN_SQL_KEYWORDS = Set.of(
            "DROP", "TRUNCATE", "DELETE", "UPDATE", "INSERT", "ALTER", "CREATE");

    public void validateNaturalText(String naturalText) {
        if (StringUtils.isBlank(naturalText)) {
            throw new QueryException(ErrorCode.EMPTY_QUERY);
        }

        if (naturalText.length() > 200) {
            throw new QueryException(ErrorCode.QUERY_TOO_LONG);
        }

        // SQL 키워드 필터링
        if (containsForbiddenKeywords(naturalText)) {
            throw new QueryException(ErrorCode.FORBIDDEN_KEYWORD);
        }

        // UTF-8 유효성 검증
        if (!isValidUtf8(naturalText)) {
            throw new QueryException(ErrorCode.INVALID_CHARACTER);
        }
    }

    private boolean containsForbiddenKeywords(String text) {
        String upperText = text.toUpperCase();
        return FORBIDDEN_SQL_KEYWORDS.stream()
                .anyMatch(upperText::contains);
    }

    private boolean isValidUtf8(String text) {
        try {
            byte[] bytes = text.getBytes(StandardCharsets.UTF_8);
            return Arrays.equals(bytes, text.getBytes(StandardCharsets.UTF_8));
        } catch (Exception e) {
            return false;
        }
    }
}