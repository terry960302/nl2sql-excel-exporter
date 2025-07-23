package com.pandaterry.presentation.dto.response;

import com.pandaterry.msa_contracts.vo.schema.TableSchema;
import io.micronaut.serde.annotation.Serdeable;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Serdeable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class ScanSchemaResponse {
    private UUID datasourceId;
    private List<TableSchema> schemas;
    private String rawSchema;
}

