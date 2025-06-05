package com.pandaterry.schema_microservice.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pandaterry.msa_contracts.dto.schema.request.DatasourceUpdateRequest;
import com.pandaterry.msa_contracts.dto.schema.request.RegisterSchemaRequest;
import com.pandaterry.msa_contracts.vo.schema.ColumnSchema;
import com.pandaterry.msa_contracts.vo.schema.TableSchema;
import com.pandaterry.schema_microservice.application.service.DatasourceService;
import com.pandaterry.schema_microservice.application.service.SchemaService;
import com.pandaterry.msa_contracts.dto.schema.response.DatasourceResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
@Transactional
class SchemaIntegrationTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private SchemaService schemaService;
    @Autowired
    private DatasourceService datasourceService;

    @Test
    @DisplayName("스키마 업로드 후 데이터소스 활성화")
    void schema_and_datasource_flow() {
        UUID orgId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        UUID agentId = UUID.randomUUID();

        DatasourceResponse init = datasourceService.initDatasource(orgId, userId, agentId);
        UUID datasourceId = init.getId();

        DatasourceUpdateRequest update = DatasourceUpdateRequest.builder()
                .name("ds")
                .dbType(null)
                .engineType(null)
                .build();
        datasourceService.activateDatasource(datasourceId, orgId, userId, agentId, update);

        ColumnSchema column = ColumnSchema.create("id", "INT", false, true);
        TableSchema table = TableSchema.create("t1", List.of(column));
        RegisterSchemaRequest req = new RegisterSchemaRequest(orgId, userId, agentId, datasourceId, "schema", List.of(table), "{}");

        assertThat(schemaService.uploadSchema(orgId, userId, agentId, req)).isNotNull();
    }
}
