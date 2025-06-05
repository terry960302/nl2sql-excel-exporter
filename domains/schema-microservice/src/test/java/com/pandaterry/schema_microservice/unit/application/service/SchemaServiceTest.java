import com.pandaterry.msa_contracts.dto.schema.request.RegisterSchemaRequest;
import com.pandaterry.msa_contracts.dto.schema.response.RegisterSchemaResponse;
import com.pandaterry.msa_contracts.vo.schema.ColumnSchema;
import com.pandaterry.msa_contracts.vo.schema.TableSchema;
import com.pandaterry.schema_microservice.application.service.SchemaService;
import com.pandaterry.schema_microservice.domain.entity.ColumnDefinition;
import com.pandaterry.schema_microservice.domain.entity.Schema;
import com.pandaterry.schema_microservice.domain.entity.TableDefinition;
import com.pandaterry.schema_microservice.domain.exception.ErrorCode;
import com.pandaterry.schema_microservice.domain.exception.SchemaException;
import com.pandaterry.schema_microservice.infrastructure.repository.ColumnDefinitionRepository;
import com.pandaterry.schema_microservice.infrastructure.repository.SchemaRepository;
import com.pandaterry.schema_microservice.infrastructure.repository.TableDefinitionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SchemaServiceTest {
    @Mock
    private SchemaRepository schemaRepository;
    @Mock
    private ColumnDefinitionRepository columnDefinitionRepository;
    @Mock
    private TableDefinitionRepository tableDefinitionRepository;

    @InjectMocks
    private SchemaService schemaService;

    private UUID orgId;
    private UUID userId;
    private UUID agentId;
    private RegisterSchemaRequest request;

    @BeforeEach
    void setUp() {
        orgId = UUID.randomUUID();
        userId = UUID.randomUUID();
        agentId = UUID.randomUUID();
        ColumnSchema column = ColumnSchema.create("id", "INTEGER", false, true);
        TableSchema table = TableSchema.create("test", List.of(column));
        request = new RegisterSchemaRequest(orgId, userId, agentId, UUID.randomUUID(), "schema", List.of(table), "{}");
    }

    @Test
    @DisplayName("스키마 업로드 성공")
    void uploadSchema_성공() {
        Schema saved = Schema.create(orgId, request.datasourceId(), userId, request.name(), request.rawSchema());
        saved.setId(UUID.randomUUID());
        when(schemaRepository.save(any(Schema.class))).thenReturn(saved);
        when(tableDefinitionRepository.save(any(TableDefinition.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(columnDefinitionRepository.save(any(ColumnDefinition.class))).thenAnswer(invocation -> invocation.getArgument(0));

        RegisterSchemaResponse response = schemaService.uploadSchema(orgId, userId, agentId, request);

        assertThat(response.id()).isNotNull();
        verify(schemaRepository).save(any(Schema.class));
        verify(tableDefinitionRepository, atLeastOnce()).save(any(TableDefinition.class));
        verify(columnDefinitionRepository, atLeastOnce()).save(any(ColumnDefinition.class));
    }

    @Test
    @DisplayName("조직 ID 누락으로 인한 스키마 업로드 실패")
    void uploadSchema_OrgIdNull_실패() {
        assertThatThrownBy(() -> schemaService.uploadSchema(null, userId, agentId, request))
                .isInstanceOf(SchemaException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.ORG_ID_NOT_FOUND);
    }
}
