package com.pandaterry.msa_contracts.dto.schema.response;

import io.micronaut.serde.annotation.Serdeable;
import lombok.*;

import java.util.UUID;

@Serdeable
@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
public class RegisterSchemaResponse {
    private UUID id;
    private UUID datasourceId;
    private String name;
    private String rawJson;
}
