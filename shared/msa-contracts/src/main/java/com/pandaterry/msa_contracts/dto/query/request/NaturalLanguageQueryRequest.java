package com.pandaterry.msa_contracts.dto.query.request;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.util.UUID;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class NaturalLanguageQueryRequest {
    @NonNull
    private String naturalText;
    @NonNull
    private UUID datasourceId;
    @NonNull
    private UUID agentId;
    @NonNull
    private UUID orgId;
    @NonNull
    private UUID userId;
}