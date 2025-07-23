package com.pandaterry.presentation.dto.request;

import com.pandaterry.msa_contracts.enums.schema.DatabaseEngineType;
import io.micronaut.serde.annotation.Serdeable;
import lombok.*;

import java.util.UUID;

@Serdeable
@Getter
@Setter
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor
@Builder
public class ScanSchemaRequest {
    private DatabaseEngineType engineType;
    private UUID datasourceId;
}
