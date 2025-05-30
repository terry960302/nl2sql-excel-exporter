package com.pandaterry.query_microservice.unit.application.service;

import com.pandaterry.query_microservice.application.dto.*;
import com.pandaterry.query_microservice.application.service.PromptService;
import com.pandaterry.query_microservice.domain.enums.ErrorCode;
import com.pandaterry.query_microservice.domain.exception.QueryException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

class PromptServiceTest {

        private PromptService promptService;

        @BeforeEach
        void setUp() {
                promptService = new PromptService();
        }

        @Test
        void generatePrompt_WithValidContext_ShouldGeneratePrompt() {
                // Given
                SchemaInfo schema = SchemaInfo.builder()
                                .schemaName("test_schema")
                                .tables(Collections.singletonList(
                                                TableInfo.builder()
                                                                .tableName("test_table")
                                                                .columns(Arrays.asList(
                                                                                ColumnInfo.builder()
                                                                                                .columnName("id")
                                                                                                .dataType("INTEGER")
                                                                                                .build(),
                                                                                ColumnInfo.builder()
                                                                                                .columnName("name")
                                                                                                .dataType("VARCHAR")
                                                                                                .build()))
                                                                .build()))
                                .aliases(Collections.singletonList(
                                                AliasInfo.builder()
                                                                .alias("tt")
                                                                .build()))
                                .build();

                PromptContext context = PromptContext.builder()
                                .schemas(Collections.singletonList(schema))
                                .naturalText("테스트 쿼리")
                                .build();

                // When
                String prompt = promptService.generatePrompt(context);

                // Then
                assertNotNull(prompt);
                assertTrue(prompt.contains("test_schema"));
                assertTrue(prompt.contains("test_table"));
                assertTrue(prompt.contains("id"));
                assertTrue(prompt.contains("name"));
                assertTrue(prompt.contains("tt"));
                assertTrue(prompt.contains("테스트 쿼리"));
        }

        @Test
        void generatePrompt_WithEmptySchemas_ShouldThrowException() {
                // Given
                PromptContext context = PromptContext.builder()
                                .schemas(Collections.emptyList())
                                .naturalText("테스트 쿼리")
                                .build();

                // When & Then
                QueryException exception = assertThrows(QueryException.class,
                                () -> promptService.generatePrompt(context));
                assertEquals(ErrorCode.PROMPT_CONTEXT_INCOMPLETE, exception.getErrorCode());
                assertEquals(ErrorCode.PROMPT_CONTEXT_INCOMPLETE.getMessage(), exception.getMessage());
        }

        @Test
        void generatePrompt_WithNullSchemas_ShouldThrowException() {
                // Given
                PromptContext context = PromptContext.builder()
                                .schemas(null)
                                .naturalText("테스트 쿼리")
                                .build();

                // When & Then
                QueryException exception = assertThrows(QueryException.class,
                                () -> promptService.generatePrompt(context));
                assertEquals(ErrorCode.PROMPT_CONTEXT_INCOMPLETE, exception.getErrorCode());
                assertEquals(ErrorCode.PROMPT_CONTEXT_INCOMPLETE.getMessage(), exception.getMessage());
        }
}