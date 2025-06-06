package com.pandaterry.query_microservice.application.vo;

import lombok.Builder;
import lombok.Getter;

import java.util.List;
import java.util.stream.Collectors;

import com.pandaterry.msa_contracts.dto.query.response.SchemaInfoResponse;

@Getter
@Builder
public class SchemaInfo {
        private final String schemaName;
        private final List<TableInfo> tables;
        private final List<AliasInfo> aliases;

        public static SchemaInfo from(SchemaInfoResponse response) {
                return SchemaInfo.builder()
                                .schemaName(response.getSchemaName())
                                .tables(response.getTables().stream()
                                                .map(TableInfo::from)
                                                .collect(Collectors.toList()))
                                .aliases(response.getAliases().stream()
                                                .map(AliasInfo::from)
                                                .collect(Collectors.toList()))
                                .build();
        }
}