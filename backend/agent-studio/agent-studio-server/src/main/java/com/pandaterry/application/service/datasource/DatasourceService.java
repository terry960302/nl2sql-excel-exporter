package com.pandaterry.application.service.datasource;

import com.pandaterry.infrastructure.client.DefaultDatasourceClient;
import com.pandaterry.infrastructure.client.DefaultSchemaClient;
import com.pandaterry.msa_contracts.dto.schema.request.RegisterSchemaRequest;
import com.pandaterry.msa_contracts.dto.schema.response.DatasourceResponse;
import com.pandaterry.msa_contracts.dto.schema.response.RegisterSchemaResponse;
import com.pandaterry.msa_contracts.vo.schema.TableSchema;
import com.pandaterry.presentation.dto.JdbcUrlBuilder;
import com.pandaterry.presentation.dto.request.DatabaseConnectionRequest;
import com.pandaterry.presentation.dto.request.ScanSchemaRequest;
import com.pandaterry.presentation.dto.response.DatabaseConnectionResponse;
import com.pandaterry.presentation.dto.response.ScanSchemaResponse;
import com.pandaterry.domain.enums.ConnectionStatus;
import com.pandaterry.domain.model.datasource.DatasourceSession;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;

import java.util.List;

@Slf4j
@Singleton
public class DatasourceService {

    @Inject
    private DataSourceManager dataSourceManager;

    @Inject
    private SchemaScanner schemaScanner;

    public DatabaseConnectionResponse testConnect(DatabaseConnectionRequest req) {
        String jdbcUrl = new JdbcUrlBuilder(req.getEngineType())
                .database(req.getName())
                .host(req.getHost())
                .port(req.getPort())
                .username(req.getUsername())
                .password(req.getPassword())
                .build();
        DatasourceSession session = DatasourceSession.create(jdbcUrl, req.getUsername(), req.getPassword(), req.getEngineType());
        dataSourceManager.register(session);
        dataSourceManager.testConnection(session.getDatasourceId());

        return new DatabaseConnectionResponse(session.getDatasourceId(), session.getJdbcUrl(), ConnectionStatus.CONNECTED);
    }

    public ScanSchemaResponse scanSchema(ScanSchemaRequest req) {
        List<TableSchema> schemas = schemaScanner.scanSchema(req.getEngineType(), req.getDatasourceId());
        String rawSchema = schemaScanner.scanRawSchema(req.getEngineType(), req.getDatasourceId());

        return new ScanSchemaResponse(req.getDatasourceId(), schemas, rawSchema);
    }
}
