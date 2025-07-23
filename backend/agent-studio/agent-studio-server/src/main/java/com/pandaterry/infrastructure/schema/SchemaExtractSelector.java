package com.pandaterry.infrastructure.schema;

import com.pandaterry.application.exception.AgentException;
import com.pandaterry.domain.enums.ErrorCode;
import com.pandaterry.domain.service.SchemaExtractor;
import com.pandaterry.msa_contracts.enums.schema.DatabaseEngineType;
import com.pandaterry.msa_contracts.enums.schema.DatabaseType;
import jakarta.inject.Singleton;

import java.util.List;

@Singleton
public class SchemaExtractSelector {

    private final List<SchemaExtractor> extractors;

    public SchemaExtractSelector(List<SchemaExtractor> extractors) {
        this.extractors = extractors;
    }

    public SchemaExtractor getExtractor(DatabaseEngineType dbType) {
        return extractors.stream()
                .filter(e -> e.supports(dbType))
                .findFirst()
                .orElseThrow(() -> new AgentException(ErrorCode.INVALID_DATABASE_TYPE));
    }
}
