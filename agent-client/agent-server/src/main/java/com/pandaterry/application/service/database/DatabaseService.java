package com.pandaterry.application.service.database;

import com.pandaterry.msa_contracts.dto.schema.request.RegisterSchemaRequest;
import com.pandaterry.msa_contracts.dto.schema.response.RegisterSchemaResponse;
import com.pandaterry.presentation.dto.request.DatabaseConnectionRequest;
import com.pandaterry.presentation.dto.request.ScanSchemaRequest;
import com.pandaterry.presentation.dto.response.DatabaseConnectionResponse;
import com.pandaterry.presentation.dto.response.ScanSchemaResponse;
import com.pandaterry.domain.enums.ConnectionStatus;
import com.pandaterry.domain.model.database.DatasourceSession;
import com.pandaterry.domain.model.database.TableSchema;
import com.pandaterry.infrastructure.client.SchemaServiceClient;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;

import java.util.List;

@Singleton
public class DatabaseService {

    @Inject
    private DataSourceManager dataSourceManager;

    @Inject
    SchemaServiceClient schemaServiceClient;

    @Inject
    SchemaScanner schemaScanner;

    public DatabaseConnectionResponse testConnect(DatabaseConnectionRequest req) {
        DatasourceSession session = DatasourceSession.create(req.jdbcUrl(), req.username(), req.password(), req.type());
        dataSourceManager.register(session);
        dataSourceManager.testConnection(session.getDatasourceId());

        return new DatabaseConnectionResponse(session.getDatasourceId(), session.getJdbcUrl(), ConnectionStatus.CONNECTED);
    }

    public ScanSchemaResponse scanSchema(ScanSchemaRequest req){
        List<TableSchema> schemas =  schemaScanner.scanSchema(req.datasourceId());
        String rawSchema = schemaScanner.scanRawSchema(req.datasourceId());

        return new ScanSchemaResponse(req.datasourceId(), schemas, rawSchema);
    }

    public RegisterSchemaResponse registerSchema(RegisterSchemaRequest req){
        return schemaServiceClient.uploadSchema(req);
    }
}
