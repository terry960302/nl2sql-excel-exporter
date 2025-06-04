package com.pandaterry.query_microservice.application.vo;

import com.pandaterry.query_microservice.application.dto.response.AliasInfoResponse;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AliasInfo {
    private final String alias;

    public static AliasInfo from(AliasInfoResponse response) {
        return AliasInfo.builder()
                .alias(response.getAlias())
                .build();
    }
}