package com.pandaterry.query_microservice.application.service;

import com.pandaterry.query_microservice.application.dto.PromptContext;
import com.pandaterry.query_microservice.application.dto.PromptFragment;
import com.pandaterry.query_microservice.application.dto.SchemaInfo;
import com.pandaterry.query_microservice.application.dto.TableInfo;
import com.pandaterry.query_microservice.application.dto.ColumnInfo;
import com.pandaterry.query_microservice.application.dto.AliasInfo;
import com.pandaterry.query_microservice.domain.enums.ErrorCode;
import com.pandaterry.query_microservice.domain.exception.QueryException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PromptService {

    public String generatePrompt(PromptContext context) {
        try {
            StringBuilder prompt = new StringBuilder();

            // 1. 인트로
            prompt.append(PromptFragment.INTRO.getTemplate()).append("\n\n");

            // 2. 스키마 정보
            String schemaInfo = formatSchemaInfo(context.getSchemas());
            prompt.append(String.format(PromptFragment.SCHEMA_DEFINITION.getTemplate(), schemaInfo)).append("\n\n");

            // 3. 자연어 요청
            prompt.append(String.format(PromptFragment.NATURAL_LANGUAGE.getTemplate(), context.getNaturalText()))
                    .append("\n\n");

            // 4. 제약 조건
            prompt.append(PromptFragment.CONSTRAINTS.getTemplate()).append("\n\n");

            // 5. 예시
            prompt.append(PromptFragment.EXAMPLE.getTemplate());

            return prompt.toString();
        } catch (QueryException e) {
            throw e;
        } catch (Exception e) {
            throw new QueryException(ErrorCode.PROMPT_ASSEMBLY_FAILURE);
        }
    }

    private String formatSchemaInfo(List<SchemaInfo> schemas) {
        if (schemas == null || schemas.isEmpty()) {
            throw new QueryException(ErrorCode.PROMPT_CONTEXT_INCOMPLETE);
        }

        StringBuilder schemaInfo = new StringBuilder();
        for (SchemaInfo schema : schemas) {
            schemaInfo.append("Schema: ").append(schema.getSchemaName()).append("\n");

            // 테이블 정보
            if (schema.getTables() != null) {
                for (TableInfo table : schema.getTables()) {
                    schemaInfo.append("  Table: ").append(table.getTableName()).append("\n");

                    // 컬럼 정보
                    if (table.getColumns() != null) {
                        for (ColumnInfo column : table.getColumns()) {
                            schemaInfo.append("    Column: ")
                                    .append(column.getColumnName())
                                    .append(" (").append(column.getDataType()).append(")\n");
                        }
                    }
                }
            }

            // 별칭 정보
            if (schema.getAliases() != null) {
                schemaInfo.append("  Aliases:\n");
                for (AliasInfo alias : schema.getAliases()) {
                    schemaInfo.append("    ").append(alias.getAlias()).append("\n");
                }
            }

            schemaInfo.append("\n");
        }

        return schemaInfo.toString();
    }
}