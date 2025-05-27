package com.pandaterry.schema_microservice.integration.datasource;

import com.pandaterry.schema_microservice.domain.entity.Datasource;
import com.pandaterry.schema_microservice.domain.enumerated.DatabaseEngineType;
import com.pandaterry.schema_microservice.domain.enumerated.DatabaseType;

import java.util.UUID;

public class DatasourceTestDataFactory {

    public static Datasource createTestDatasource(UUID orgId) {
        return Datasource.create(
                orgId,
                "test-datasource",
                DatabaseType.RDB,
                DatabaseEngineType.POSTGRESQL,
                "localhost:5432",
                "test_user",
                "test_password",
                true,
                null);
    }

    public static Datasource createTestDatasourceWithCustomName(UUID orgId, String name) {
        return Datasource.create(
                orgId,
                name,
                DatabaseType.RDB,
                DatabaseEngineType.POSTGRESQL,
                "localhost:5432",
                "test_user",
                "test_password",
                true,
                null);
    }
}