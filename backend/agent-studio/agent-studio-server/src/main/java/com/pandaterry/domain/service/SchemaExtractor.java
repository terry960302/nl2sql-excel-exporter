package com.pandaterry.domain.service;

import com.pandaterry.application.exception.AgentException;
import com.pandaterry.msa_contracts.enums.schema.DatabaseEngineType;
import com.pandaterry.msa_contracts.vo.schema.TableSchema;

import java.sql.Connection;
import java.util.List;

public interface SchemaExtractor {
    List<TableSchema> extractSchema(Connection connection) throws AgentException;
    String extractRawSchema(Connection connection) throws AgentException;

    boolean supports(DatabaseEngineType engineType);
}