package com.pandaterry.schema_microservice.integration.alias;

import com.pandaterry.schema_microservice.domain.entity.Alias;

import java.util.UUID;

public class AliasTestDataFactory {

    public static Alias createTestAlias(UUID columnId) {
        return Alias.create(
                columnId,
                "test-alias",
                "Test Alias Description");
    }

    public static Alias createTestAliasWithCustomName(UUID columnId, String aliasName) {
        return Alias.create(
                columnId,
                aliasName,
                "Test Alias Description");
    }
}