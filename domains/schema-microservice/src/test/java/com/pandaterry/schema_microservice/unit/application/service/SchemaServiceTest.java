package com.pandaterry.schema_microservice.unit.application.service;

import com.pandaterry.schema_microservice.application.service.SchemaService;
import com.pandaterry.schema_microservice.domain.entity.Schema;
import com.pandaterry.schema_microservice.domain.enumerated.EnableStatus;
import com.pandaterry.schema_microservice.domain.exception.ErrorCode;
import com.pandaterry.schema_microservice.domain.exception.SchemaException;
import com.pandaterry.schema_microservice.infrastructure.repository.SchemaRepository;
import com.pandaterry.schema_microservice.presentation.dto.SchemaCreateRequest;
import com.pandaterry.schema_microservice.presentation.dto.SchemaResponse;
import com.pandaterry.schema_microservice.presentation.dto.SchemaUpdateRequest;
import org.junit.jupiter.api.BeforeEach;
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

    @InjectMocks
    private SchemaService schemaService;

    private UUID orgId;
    private UUID userId;
    private UUID datasourceId;
    private UUID schemaId;
    private Schema schema;

    @BeforeEach
    void setUp() {
        orgId = UUID.randomUUID();
        userId = UUID.randomUUID();
        datasourceId = UUID.randomUUID();
        schemaId = UUID.randomUUID();
        schema = Schema.create(orgId, datasourceId, userId, "test_schema");
    }

    @Test
    void createSchema_성공() {
        // given
        SchemaCreateRequest request = SchemaCreateRequest.of(datasourceId, "new_schema");
        when(schemaRepository.save(any(Schema.class))).thenReturn(schema);

        // when
        SchemaResponse result = schemaService.createSchema(request, orgId, userId);

        // then
        assertThat(result.getName()).isEqualTo("test_schema");
        verify(schemaRepository).save(any(Schema.class));
    }

    @Test
    void getSchemasByDatasource_성공() {
        // given
        List<Schema> schemas = List.of(schema);
        when(schemaRepository.findByDatasourceIdAndIsEnabled(datasourceId, EnableStatus.ENABLED)).thenReturn(schemas);

        // when
        List<SchemaResponse> result = schemaService.getSchemasByDatasource(datasourceId);

        // then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("test_schema");
    }

    @Test
    void getSchema_성공() {
        // given
        when(schemaRepository.findById(schemaId)).thenReturn(Optional.of(schema));

        // when
        SchemaResponse result = schemaService.getSchema(schemaId);

        // then
        assertThat(result.getName()).isEqualTo("test_schema");
    }

    @Test
    void getSchema_실패_스키마없음() {
        // given
        when(schemaRepository.findById(schemaId)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> schemaService.getSchema(schemaId))
                .isInstanceOf(SchemaException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.SCHEMA_NOT_FOUND);
    }

    @Test
    void updateSchema_성공() {
        // given
        SchemaUpdateRequest request = SchemaUpdateRequest.of("updated_schema", EnableStatus.ENABLED);
        when(schemaRepository.findById(schemaId)).thenReturn(Optional.of(schema));
        when(schemaRepository.save(schema)).thenReturn(schema);

        // when
        SchemaResponse result = schemaService.updateSchema(schemaId, request);

        // then
        assertThat(result.getName()).isEqualTo("updated_schema");
        verify(schemaRepository).save(any(Schema.class));
    }

    @Test
    void updateSchema_실패_스키마없음() {
        // given
        SchemaUpdateRequest request = SchemaUpdateRequest.of("updated_schema", EnableStatus.ENABLED);
        when(schemaRepository.findById(schemaId)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> schemaService.updateSchema(schemaId, request))
                .isInstanceOf(SchemaException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.SCHEMA_NOT_FOUND);
    }

    @Test
    void deactivateSchema_성공() {
        // given
        when(schemaRepository.findById(schemaId)).thenReturn(Optional.of(schema));

        // when
        schemaService.deactivateSchema(schemaId);

        // then
        assertThat(schema.getIsEnabled()).isEqualTo(EnableStatus.DISABLED);
    }

    @Test
    void deactivateSchema_실패_스키마없음() {
        // given
        when(schemaRepository.findById(schemaId)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> schemaService.deactivateSchema(schemaId))
                .isInstanceOf(SchemaException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.SCHEMA_NOT_FOUND);
    }
}