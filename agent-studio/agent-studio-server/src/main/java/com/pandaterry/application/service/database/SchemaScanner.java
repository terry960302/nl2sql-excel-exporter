package com.pandaterry.application.service.database;

import com.pandaterry.application.exception.AgentException;
import com.pandaterry.domain.model.database.*;
import com.pandaterry.domain.service.SchemaExtractor;
import com.pandaterry.domain.enums.ErrorCode;
import jakarta.inject.Singleton;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

@Singleton
public class SchemaScanner {

    private final SchemaExtractor schemaExtractor;
    private final DataSourceManager dataSourceManager;

    public SchemaScanner(
            final SchemaExtractor schemaExtractor,
            final DataSourceManager dataSourceManager) {
        this.schemaExtractor = schemaExtractor;
        this.dataSourceManager = dataSourceManager;
    }

    public List<TableSchema> scanSchema(UUID datasourceId) {
       try(Connection connection = dataSourceManager.getConnection(datasourceId)){
           return schemaExtractor.extractSchema(connection);
       }catch(SQLException e){
          throw new AgentException(ErrorCode.DATABASE_SCHEMA_SCAN_FAILED, e);
       }
    }

    public String scanRawSchema(UUID datasourceId) {
        try(Connection connection = dataSourceManager.getConnection(datasourceId)){
            return schemaExtractor.extractRawSchema(connection);
        }catch(SQLException e){
            throw new AgentException(ErrorCode.DATABASE_SCHEMA_SCAN_FAILED, e);
        }
    }
}