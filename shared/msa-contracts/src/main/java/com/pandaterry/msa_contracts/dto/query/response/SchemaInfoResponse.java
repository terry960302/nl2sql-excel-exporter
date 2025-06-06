package com.pandaterry.msa_contracts.dto.query.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AccessLevel;

import java.util.List;
import java.util.UUID;


@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class SchemaInfoResponse {
    private UUID schemaId;
    private String schemaName;
    private List<TableInfoResponse> tables;
    private List<AliasInfoResponse> aliases;
}