package com.pandaterry.query_microservice.application.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AliasInfo {
    private final String alias;
}