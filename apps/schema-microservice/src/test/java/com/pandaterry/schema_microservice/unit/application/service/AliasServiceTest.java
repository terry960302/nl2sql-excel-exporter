package com.pandaterry.schema_microservice.unit.application.service;

import com.pandaterry.schema_microservice.application.service.AliasService;
import com.pandaterry.schema_microservice.domain.entity.Alias;
import com.pandaterry.schema_microservice.domain.enumerated.EnableStatus;
import com.pandaterry.schema_microservice.domain.exception.ErrorCode;
import com.pandaterry.schema_microservice.domain.exception.SchemaException;
import com.pandaterry.schema_microservice.infrastructure.repository.AliasRepository;
import com.pandaterry.schema_microservice.presentation.dto.AliasCreateRequest;
import com.pandaterry.schema_microservice.presentation.dto.AliasResponse;
import com.pandaterry.schema_microservice.presentation.dto.AliasUpdateRequest;
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
class AliasServiceTest {

    @Mock
    private AliasRepository aliasRepository;

    @InjectMocks
    private AliasService aliasService;

    private UUID columnId;
    private UUID aliasId;
    private Alias alias;

    @BeforeEach
    void setUp() {
        columnId = UUID.randomUUID();
        aliasId = UUID.randomUUID();
        alias = Alias.create(columnId, "test_alias", "test description");
    }

    @Test
    void getAliasesByColumn_성공() {
        // given
        List<Alias> aliases = List.of(alias);
        when(aliasRepository.findByColumnIdAndIsEnabled(columnId, EnableStatus.ENABLED)).thenReturn(aliases);

        // when
        List<AliasResponse> result = aliasService.getAliasesByColumn(columnId);

        // then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getAliasName()).isEqualTo("test_alias");
    }

    @Test
    void getAlias_성공() {
        // given
        when(aliasRepository.findById(aliasId)).thenReturn(Optional.of(alias));

        // when
        AliasResponse result = aliasService.getAlias(aliasId);

        // then
        assertThat(result.getAliasName()).isEqualTo("test_alias");
    }

    @Test
    void getAlias_실패_별칭없음() {
        // given
        when(aliasRepository.findById(aliasId)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> aliasService.getAlias(aliasId))
                .isInstanceOf(SchemaException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.ALIAS_NOT_FOUND);
    }

    @Test
    void createAlias_성공() {
        // given
        AliasCreateRequest request = AliasCreateRequest.of(columnId, "new_alias", "new description");
        when(aliasRepository.save(any(Alias.class))).thenReturn(alias);

        // when
        AliasResponse result = aliasService.createAlias(request);

        // then
        assertThat(result.getAliasName()).isEqualTo("test_alias");
        verify(aliasRepository).save(any(Alias.class));
    }

    @Test
    void updateAlias_성공() {
        // given
        AliasUpdateRequest request = AliasUpdateRequest.of("updated_alias", "updated description");
        when(aliasRepository.findById(aliasId)).thenReturn(Optional.of(alias));

        // when
        AliasResponse result = aliasService.updateAlias(aliasId, request);

        // then
        assertThat(result.getAliasName()).isEqualTo("updated_alias");
    }

    @Test
    void updateAlias_실패_별칭없음() {
        // given
        AliasUpdateRequest request = AliasUpdateRequest.of("updated_alias", "updated description");
        when(aliasRepository.findById(aliasId)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> aliasService.updateAlias(aliasId, request))
                .isInstanceOf(SchemaException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.ALIAS_NOT_FOUND);
    }

    @Test
    void deactivateAlias_성공() {
        // given
        when(aliasRepository.findById(aliasId)).thenReturn(Optional.of(alias));

        // when
        aliasService.deactivateAlias(aliasId);

        // then
        assertThat(alias.getIsEnabled()).isEqualTo(EnableStatus.DISABLED);
    }

    @Test
    void deactivateAlias_실패_별칭없음() {
        // given
        when(aliasRepository.findById(aliasId)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> aliasService.deactivateAlias(aliasId))
                .isInstanceOf(SchemaException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.ALIAS_NOT_FOUND);
    }
}