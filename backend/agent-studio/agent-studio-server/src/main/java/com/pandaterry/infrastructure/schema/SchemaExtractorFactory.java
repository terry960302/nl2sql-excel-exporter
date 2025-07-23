package com.pandaterry.infrastructure.schema;

import io.micronaut.context.annotation.Factory;
import jakarta.inject.Named;
import jakarta.inject.Singleton;


@Factory
public class SchemaExtractorFactory {

    @Singleton
    @Named("mysql")
    public MySqlSchemaExtractor mySqlSchemaExtractor() {
        return new MySqlSchemaExtractor();
    }

    @Singleton
    @Named("postgresql")
    public PostgresSchemaExtractor postgresSchemaExtractor() {
        return new PostgresSchemaExtractor();
    }

    @Singleton
    @Named("oracle")
    public OracleSchemaExtractor oracleSchemaExtractor() {
        return new OracleSchemaExtractor();
    }
}