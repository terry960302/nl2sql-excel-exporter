package com.pandaterry.schema_microservice.unit.domain.entity;

import com.pandaterry.msa_contracts.enums.schema.DatabaseEngineType;
import com.pandaterry.msa_contracts.enums.schema.DatabaseType;
import com.pandaterry.schema_microservice.domain.entity.Datasource;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class DatasourceTest {

    @Test
    void create_메서드가_user와_agent_정보를_저장한다() {
        // given
        UUID orgId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        UUID agentId = UUID.randomUUID();

        // when
        Datasource datasource = Datasource.create(orgId, "test", DatabaseType.RDB, DatabaseEngineType.POSTGRESQL, userId, agentId);

        // then
        assertThat(datasource.getCreatedByUser()).isEqualTo(userId);
        assertThat(datasource.getCreatedByAgent()).isEqualTo(agentId);
    }

    @Test
    void init_메서드가_user와_agent_정보를_저장한다() {
        // given
        UUID orgId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        UUID agentId = UUID.randomUUID();

        // when
        Datasource datasource = Datasource.init(orgId, userId, agentId);

        // then
        assertThat(datasource.getCreatedByUser()).isEqualTo(userId);
        assertThat(datasource.getCreatedByAgent()).isEqualTo(agentId);
    }
}
