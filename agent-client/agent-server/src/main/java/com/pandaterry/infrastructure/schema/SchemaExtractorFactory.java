package com.pandaterry.infrastructure.schema;

import com.pandaterry.domain.model.database.DatabaseType;
import com.pandaterry.domain.service.SchemaExtractor;
import com.pandaterry.application.exception.AgentException;
import com.pandaterry.domain.enums.ErrorCode;
import jakarta.inject.Singleton;
import java.util.List;

@Singleton
public class SchemaExtractorFactory {
    private final List<SchemaExtractor> extractors;

    public SchemaExtractorFactory(List<SchemaExtractor> extractors) {
        this.extractors = extractors;
    }

    public SchemaExtractor getExtractor(DatabaseType databaseType) {
        return extractors.stream()
                .filter(extractor -> extractor.supports(databaseType))
                .findFirst()
                .orElseThrow(() -> new AgentException(ErrorCode.INVALID_DATABASE_TYPE));
    }
}