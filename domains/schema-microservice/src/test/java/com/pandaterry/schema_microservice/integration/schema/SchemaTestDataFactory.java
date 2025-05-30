package com.pandaterry.schema_microservice.integration.schema;

import com.pandaterry.schema_microservice.domain.entity.Datasource;
import com.pandaterry.schema_microservice.domain.entity.Schema;
import com.pandaterry.schema_microservice.domain.enumerated.DatabaseEngineType;
import com.pandaterry.schema_microservice.domain.enumerated.DatabaseType;
import com.pandaterry.schema_microservice.domain.enumerated.EnableStatus;
import com.pandaterry.schema_microservice.infrastructure.repository.DatasourceRepository;
import com.pandaterry.schema_microservice.infrastructure.repository.SchemaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Component
public class SchemaTestDataFactory {

    @Autowired
    private DatasourceRepository datasourceRepository;

    @Autowired
    private SchemaRepository schemaRepository;

    public Datasource createDatasource() {
        Map<String, Object> options = new HashMap<>();
        options.put("host", "localhost");
        options.put("port", 5432);
        options.put("database", "testdb");
        options.put("username", "testuser");
        options.put("password", "testpass");

        return Datasource.create(
                UUID.randomUUID(), // orgId
                "Test Datasource",
                DatabaseType.RDB,
                DatabaseEngineType.POSTGRESQL,
                "localhost:5432",
                "testuser",
                "testpass",
                true,
                options);
    }

    public Schema createSchema(Datasource datasource) {
        Schema schema = Schema.create(
                UUID.randomUUID(), // orgId
                datasource.getId(),
                UUID.randomUUID(), // createdBy
                "Test Schema");

        return schemaRepository.save(schema);
    }
}