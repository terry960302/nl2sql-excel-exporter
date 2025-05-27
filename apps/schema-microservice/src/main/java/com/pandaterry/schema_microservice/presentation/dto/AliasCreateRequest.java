package com.pandaterry.schema_microservice.presentation.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

import com.pandaterry.schema_microservice.domain.entity.Alias;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AliasCreateRequest {
    @NotNull(message = "컬럼 ID는 필수입니다")
    private UUID columnId;

    @NotBlank(message = "별칭 이름은 필수입니다")
    private String aliasName;

    private String description;

    public static AliasCreateRequest of(UUID columnId, String aliasName, String description) {
        return AliasCreateRequest.builder()
                .columnId(columnId)
                .aliasName(aliasName)
                .description(description)
                .build();
    }

    public Alias toEntity() {
        return Alias.create(columnId, aliasName, description);
    }
}