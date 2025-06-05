import com.pandaterry.msa_contracts.dto.schema.request.DatasourceUpdateRequest;
import com.pandaterry.msa_contracts.dto.schema.response.DatasourceResponse;
import com.pandaterry.msa_contracts.enums.schema.DatabaseEngineType;
import com.pandaterry.msa_contracts.enums.schema.DatabaseType;
import com.pandaterry.msa_contracts.enums.schema.EnableStatus;
import com.pandaterry.schema_microservice.application.service.DatasourceService;
import com.pandaterry.schema_microservice.domain.entity.Datasource;
import com.pandaterry.schema_microservice.domain.exception.ErrorCode;
import com.pandaterry.schema_microservice.domain.exception.SchemaException;
import com.pandaterry.schema_microservice.infrastructure.repository.DatasourceRepository;
import com.pandaterry.schema_microservice.presentation.mappers.DatasourceMapper;
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
class DatasourceServiceTest {
    @Mock
    private DatasourceRepository datasourceRepository;
    @InjectMocks
    private DatasourceService datasourceService;

    private UUID orgId;
    private UUID userId;
    private UUID agentId;
    private Datasource datasource;

    @BeforeEach
    void setUp() {
        orgId = UUID.randomUUID();
        userId = UUID.randomUUID();
        agentId = UUID.randomUUID();
        datasource = Datasource.init(orgId, userId, agentId);
        datasource.setId(UUID.randomUUID());
        datasource.activate();
    }

    @Test
    @DisplayName("데이터소스 초기화 성공")
    void initDatasource_성공() {
        when(datasourceRepository.save(any(Datasource.class))).thenReturn(datasource);

        DatasourceResponse response = datasourceService.initDatasource(orgId, userId, agentId);

        assertThat(response.getId()).isNotNull();
        verify(datasourceRepository).save(any(Datasource.class));
    }

    @Test
    @DisplayName("조직 ID 누락으로 초기화 실패")
    void initDatasource_OrgIdNull_실패() {
        assertThatThrownBy(() -> datasourceService.initDatasource(null, userId, agentId))
                .isInstanceOf(SchemaException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.ORG_ID_NOT_FOUND);
    }

    @Test
    @DisplayName("데이터소스 활성화 성공")
    void activateDatasource_성공() {
        DatasourceUpdateRequest request = DatasourceMapper.of("test", DatabaseType.RDB, DatabaseEngineType.POSTGRESQL);
        when(datasourceRepository.findById(datasource.getId())).thenReturn(Optional.of(datasource));
        when(datasourceRepository.save(any(Datasource.class))).thenReturn(datasource);

        DatasourceResponse response = datasourceService.activateDatasource(datasource.getId(), orgId, userId, agentId, request);

        assertThat(response.getName()).isEqualTo("test");
        verify(datasourceRepository).findById(datasource.getId());
        verify(datasourceRepository).save(any(Datasource.class));
    }

    @Test
    @DisplayName("데이터소스 조회 성공")
    void getDatasource_성공() {
        when(datasourceRepository.findById(datasource.getId())).thenReturn(Optional.of(datasource));

        DatasourceResponse response = datasourceService.getDatasource(datasource.getId());

        assertThat(response.getId()).isEqualTo(datasource.getId());
    }

    @Test
    @DisplayName("존재하지 않는 데이터소스 조회 실패")
    void getDatasource_없음_실패() {
        when(datasourceRepository.findById(datasource.getId())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> datasourceService.getDatasource(datasource.getId()))
                .isInstanceOf(SchemaException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.DATASOURCE_NOT_FOUND);
    }

    @Test
    @DisplayName("데이터소스 리스트 조회 성공")
    void getDatasources_성공() {
        when(datasourceRepository.findByOrgIdAndIsEnabled(orgId, EnableStatus.ENABLED)).thenReturn(List.of(datasource));

        List<DatasourceResponse> responses = datasourceService.getDatasources(orgId);

        assertThat(responses).hasSize(1);
        verify(datasourceRepository).findByOrgIdAndIsEnabled(orgId, EnableStatus.ENABLED);
    }

    @Test
    @DisplayName("존재하지 않는 데이터소스 비활성화 실패")
    void deactivateDatasource_없음_실패() {
        when(datasourceRepository.findById(datasource.getId())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> datasourceService.deactivateDatasource(datasource.getId()))
                .isInstanceOf(SchemaException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.DATASOURCE_NOT_FOUND);
    }

    @Test
    @DisplayName("데이터소스 비활성화 성공")
    void deactivateDatasource_성공() {
        when(datasourceRepository.findById(datasource.getId())).thenReturn(Optional.of(datasource));

        datasourceService.deactivateDatasource(datasource.getId());

        verify(datasourceRepository).findById(datasource.getId());
        assertThat(datasource.getIsEnabled()).isEqualTo(EnableStatus.DISABLED);
    }
}
