package com.pandaterry.application.service.database;

import com.pandaterry.application.dto.request.DatabaseConnectionRequest;
import com.pandaterry.application.dto.request.RegisterSchemaRequest;
import com.pandaterry.application.dto.request.ScanSchemaRequest;
import com.pandaterry.application.dto.response.DatabaseConnectionResponse;
import com.pandaterry.application.dto.response.RegisterSchemaResponse;
import com.pandaterry.application.dto.response.ScanSchemaResponse;
import com.pandaterry.domain.enums.ConnectionStatus;
import com.pandaterry.domain.model.database.DatasourceSession;
import com.pandaterry.domain.model.database.TableSchema;
import com.pandaterry.infrastructure.client.QueryServiceClient;
import com.pandaterry.infrastructure.client.SchemaServiceClient;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

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
        schemaServiceClient.uploadSchema(req);

        return new RegisterSchemaResponse();
    }
}
