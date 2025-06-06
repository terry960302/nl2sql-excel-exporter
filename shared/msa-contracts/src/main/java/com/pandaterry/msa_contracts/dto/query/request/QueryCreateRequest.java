package com.pandaterry.msa_contracts.dto.query.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AccessLevel;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class QueryCreateRequest {
    @NotBlank(message = "자연어 질의는 필수입니다")
    @Size(max = 200, message = "자연어 질의는 200자를 초과할 수 없습니다")
    private String naturalText;
}