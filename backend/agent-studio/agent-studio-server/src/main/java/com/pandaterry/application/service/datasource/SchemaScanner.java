package com.pandaterry.application.service.datasource;

import com.pandaterry.application.exception.AgentException;
import com.pandaterry.domain.model.datasource.*;
import com.pandaterry.domain.enums.ErrorCode;
import com.pandaterry.infrastructure.schema.SchemaExtractSelector;
import com.pandaterry.msa_contracts.enums.schema.DatabaseEngineType;
import com.pandaterry.msa_contracts.vo.schema.TableSchema;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

@Singleton
public class SchemaScanner {
    @Inject
    private SchemaExtractSelector schemaExtractSelector;
    @Inject
    private DataSourceManager dataSourceManager;

    public List<TableSchema> scanSchema(DatabaseEngineType engineType, UUID datasourceId) {
       try(Connection connection = dataSourceManager.getConnection(datasourceId)){
           return schemaExtractSelector.getExtractor(engineType).extractSchema(connection);
       }catch(SQLException e){
          throw new AgentException(ErrorCode.DATABASE_SCHEMA_SCAN_FAILED, e);
       }
    }

    public String scanRawSchema(DatabaseEngineType engineType, UUID datasourceId) {
        try(Connection connection = dataSourceManager.getConnection(datasourceId)){
            return schemaExtractSelector.getExtractor(engineType).extractRawSchema(connection);
        }catch(SQLException e){
            throw new AgentException(ErrorCode.DATABASE_SCHEMA_SCAN_FAILED, e);
        }
    }
}