package com.pandaterry.query_microservice.application.service;

import com.pandaterry.query_microservice.application.dto.*;
import com.pandaterry.query_microservice.application.dto.request.NaturalLanguageQueryRequest;
import com.pandaterry.query_microservice.application.dto.response.SchemaInfoResponse;
import com.pandaterry.query_microservice.infrastructure.client.LLMClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NaturalLanguageQueryService {
    private final LLMClient llmClient;
    private final SchemaService schemaService;

    public Mono<String> convertToSQL(NaturalLanguageQueryRequest request) {
        return schemaService.getSchemasWithAliases(request.getOrgId())
                .flatMap(schemaResponses -> {
                    List<SchemaInfo> schemas = schemaResponses.stream()
                            .map(this::convertToSchemaInfo)
                            .collect(Collectors.toList());

                    PromptContext context = PromptContext.builder()
                            .schemas(schemas)
                            .naturalText(request.getNaturalText())
                            .orgId(request.getOrgId())
                            .userId(request.getUserId())
                            .build();

                    String prompt = buildPrompt(context);
                    return llmClient.generateSQL(prompt);
                });
    }

    private SchemaInfo convertToSchemaInfo(SchemaInfoResponse response) {
        List<TableInfo> tables = response.getTables().stream()
                .map(tableResponse -> TableInfo.builder()
                        .tableName(tableResponse.getTableName())
                        .columns(tableResponse.getColumns().stream()
                                .map(columnResponse -> ColumnInfo.builder()
                                        .columnName(columnResponse.getColumnName())
                                        .dataType(columnResponse.getDataType())
                                        .build())
                                .collect(Collectors.toList()))
                        .build())
                .collect(Collectors.toList());

        List<AliasInfo> aliases = response.getAliases().stream()
                .map(aliasResponse -> AliasInfo.builder()
                        .alias(aliasResponse.getAlias())
                        .build())
                .collect(Collectors.toList());

        return SchemaInfo.builder()
                .schemaName(response.getSchemaName())
                .tables(tables)
                .aliases(aliases)
                .build();
    }

    private String buildPrompt(PromptContext context) {
        StringBuilder prompt = new StringBuilder();
        prompt.append("다음은 데이터베이스 스키마 정보입니다:\n\n");

        for (SchemaInfo schema : context.getSchemas()) {
            prompt.append("스키마: ").append(schema.getSchemaName()).append("\n");
            prompt.append("테이블:\n");

            for (TableInfo table : schema.getTables()) {
                prompt.append("- ").append(table.getTableName()).append("\n");
                prompt.append("  컬럼:\n");
                for (ColumnInfo column : table.getColumns()) {
                    prompt.append("  - ").append(column.getColumnName())
                            .append(" (").append(column.getDataType()).append(")\n");
                }
            }
            prompt.append("\n");
        }

        prompt.append("다음 자연어를 SQL 쿼리로 변환해주세요:\n");
        prompt.append(context.getNaturalText());

        return prompt.toString();
    }
}