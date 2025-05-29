package com.pandaterry.query_microservice.application.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;
import java.util.UUID;

@Getter
@Builder
public class PromptContext {
    private final List<SchemaInfo> schemas;
    private final String naturalText;
    private final UUID orgId;
    private final UUID userId;
}