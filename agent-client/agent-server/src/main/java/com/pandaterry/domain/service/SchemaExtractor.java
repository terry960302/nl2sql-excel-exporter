package com.pandaterry.domain.service;

import com.pandaterry.domain.enums.DatabaseType;
import com.pandaterry.domain.model.database.TableSchema;
import com.pandaterry.application.exception.AgentException;
import java.sql.Connection;
import java.util.List;

public interface SchemaExtractor {
    List<TableSchema> extractSchema(Connection connection) throws AgentException;

    boolean supports(DatabaseType databaseType);
}