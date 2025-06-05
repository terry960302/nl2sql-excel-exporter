package com.pandaterry.query_microservice.application.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.AccessLevel;

import java.util.UUID;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class NaturalLanguageQueryRequest {
    private String naturalText;
    private UUID agentId;
    private UUID orgId;
    private UUID userId;
}