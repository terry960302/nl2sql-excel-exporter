package com.pandaterry.schema_microservice.unit.application.service;

import com.pandaterry.schema_microservice.application.service.DatasourceService;
import com.pandaterry.schema_microservice.domain.entity.Datasource;
import com.pandaterry.schema_microservice.domain.enumerated.DatabaseEngineType;
import com.pandaterry.schema_microservice.domain.enumerated.DatabaseType;
import com.pandaterry.schema_microservice.domain.enumerated.EnableStatus;
import com.pandaterry.schema_microservice.domain.exception.ErrorCode;
import com.pandaterry.schema_microservice.domain.exception.SchemaException;
import com.pandaterry.schema_microservice.infrastructure.repository.DatasourceRepository;
import com.pandaterry.schema_microservice.infrastructure.util.EncryptionUtil;
import com.pandaterry.schema_microservice.presentation.dto.DatasourceCreateRequest;
import com.pandaterry.schema_microservice.presentation.dto.DatasourceResponse;
import com.pandaterry.schema_microservice.presentation.dto.DatasourceUpdateRequest;
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
class DatasourceServiceTest {

    @Mock
    private DatasourceRepository datasourceRepository;

    @Mock
    private EncryptionUtil encryptionUtil;

    @InjectMocks
    private DatasourceService datasourceService;

    private UUID orgId;
    private UUID userId;
    private UUID datasourceId;
    private Datasource datasource;

    @BeforeEach
    void setUp() {
        orgId = UUID.randomUUID();
        userId = UUID.randomUUID();
        datasourceId = UUID.randomUUID();
        datasource = Datasource.create(
                orgId,
                "test_datasource",
                DatabaseType.RDB,
                DatabaseEngineType.POSTGRESQL,
                "localhost:5432",
                "user",
                "password",
                true,
                null);
    }

    @Test
    void createDatasource_성공() {
        // given
        DatasourceCreateRequest request = DatasourceCreateRequest.of(
                "new_datasource",
                DatabaseType.RDB,
                DatabaseEngineType.POSTGRESQL,
                "localhost:5432",
                "user",
                "password",
                true,
                null);

        when(encryptionUtil.encrypt("password")).thenReturn("encrypted_password");
        when(datasourceRepository.save(any(Datasource.class))).thenAnswer(invocation -> {
            Datasource savedDatasource = invocation.getArgument(0);
            return savedDatasource;
        });

        // when
        DatasourceResponse result = datasourceService.createDatasource(orgId, request);

        // then
        assertThat(result.getName()).isEqualTo("new_datasource");
        assertThat(result.getDbType()).isEqualTo(DatabaseType.RDB);
        assertThat(result.getEngineType()).isEqualTo(DatabaseEngineType.POSTGRESQL);
        verify(datasourceRepository).save(any(Datasource.class));
        verify(encryptionUtil).encrypt("password");
    }

    @Test
    void getDatasources_성공() {
        // given
        List<Datasource> datasources = List.of(datasource);
        when(datasourceRepository.findByOrgIdAndIsEnabled(orgId, EnableStatus.ENABLED)).thenReturn(datasources);

        // when
        List<DatasourceResponse> result = datasourceService.getDatasources(orgId);

        // then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("test_datasource");
    }

    // 복호화는 수신받는 서비스에서 할 예정. 중간 가로채더라도 암호화되어있어야함.
    @Test
    void getDatasource_성공() {
        // given
        when(datasourceRepository.findById(datasourceId)).thenReturn(Optional.of(datasource));
//        when(encryptionUtil.decrypt(anyString())).thenReturn("password");

        // when
        DatasourceResponse result = datasourceService.getDatasource(datasourceId);

        // then
        assertThat(result.getName()).isEqualTo("test_datasource");
//        verify(encryptionUtil).decrypt(anyString());
    }

    @Test
    void getDatasource_실패_데이터소스없음() {
        // given
        when(datasourceRepository.findById(datasourceId)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> datasourceService.getDatasource(datasourceId))
                .isInstanceOf(SchemaException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.DATASOURCE_NOT_FOUND);
    }

    @Test
    void updateDatasource_성공() {
        // given
        DatasourceUpdateRequest request = new DatasourceUpdateRequest(
                "updated_datasource",
                "localhost:5433",
                "new_user",
                "new_password",
                true,
                null);

        when(datasourceRepository.findById(datasourceId)).thenReturn(Optional.of(datasource));
        when(encryptionUtil.encrypt(anyString())).thenReturn("encrypted_new_password");
        when(datasourceRepository.save(any(Datasource.class))).thenAnswer(invocation -> {
            Datasource savedDatasource = invocation.getArgument(0);
            return savedDatasource;
        });

        // when
        DatasourceResponse result = datasourceService.updateDatasource(datasourceId, request);

        // then
        assertThat(result.getName()).isEqualTo("updated_datasource");
        assertThat(result.getEndpoint()).isEqualTo("localhost:5433");
        assertThat(result.isSslEnabled()).isTrue();
        verify(datasourceRepository).save(any(Datasource.class));
        verify(encryptionUtil).encrypt("new_password");
    }

    @Test
    void updateDatasource_실패_데이터소스없음() {
        // given
        DatasourceUpdateRequest request = new DatasourceUpdateRequest(
                "updated_datasource",
                null,
                null,
                null,
                true,
                null);
        when(datasourceRepository.findById(datasourceId)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> datasourceService.updateDatasource(datasourceId, request))
                .isInstanceOf(SchemaException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.DATASOURCE_NOT_FOUND);
    }

    @Test
    void deactivateDatasource_성공() {
        // given
        when(datasourceRepository.findById(datasourceId)).thenReturn(Optional.of(datasource));

        // when
        datasourceService.deactivateDatasource(datasourceId);

        // then
        assertThat(datasource.getIsEnabled()).isEqualTo(EnableStatus.DISABLED);
    }

    @Test
    void deactivateDatasource_실패_데이터소스없음() {
        // given
        when(datasourceRepository.findById(datasourceId)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> datasourceService.deactivateDatasource(datasourceId))
                .isInstanceOf(SchemaException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.DATASOURCE_NOT_FOUND);
    }
}