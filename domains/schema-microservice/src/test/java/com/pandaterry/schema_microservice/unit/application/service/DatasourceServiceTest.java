package com.pandaterry.schema_microservice.unit.application.service;

import com.pandaterry.msa_contracts.dto.schema.request.DatasourceUpdateRequest;
import com.pandaterry.msa_contracts.enums.schema.EnableStatus;
import com.pandaterry.schema_microservice.application.service.DatasourceService;
import com.pandaterry.schema_microservice.domain.entity.Datasource;
import com.pandaterry.schema_microservice.infrastructure.repository.DatasourceRepository;
import com.pandaterry.schema_microservice.presentation.mappers.DatasourceMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DatasourceServiceTest {

    @Mock
    private DatasourceRepository datasourceRepository;

    @InjectMocks
    private DatasourceService datasourceService;

    private UUID datasourceId;
    private Datasource datasource;

    @BeforeEach
    void setUp() {
        datasourceId = UUID.randomUUID();
        datasource = new Datasource();
        datasource.setId(datasourceId);
        datasource.setIsEnabled(EnableStatus.DISABLED);
    }

    @Test
    void activateDatasource_활성화() {
        DatasourceUpdateRequest request = DatasourceMapper.of("ds", null, null);

        when(datasourceRepository.findById(datasourceId)).thenReturn(Optional.of(datasource));
        when(datasourceRepository.save(any(Datasource.class))).thenAnswer(invocation -> invocation.getArgument(0));

        var response = datasourceService.activateDatasource(datasourceId, UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), request);

        assertThat(response.getIsEnabled()).isEqualTo(EnableStatus.ENABLED);
        verify(datasourceRepository).save(argThat(ds -> ds.getIsEnabled() == EnableStatus.ENABLED));
    }
}
