package com.pandaterry.schema_microservice.presentation.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AliasUpdateRequest {
    @NotBlank(message = "별칭 이름은 필수입니다")
    private String aliasName;

    private String description;

    public static AliasUpdateRequest of(String aliasName, String description) {
        return AliasUpdateRequest.builder()
                .aliasName(aliasName)
                .description(description)
                .build();
    }
}