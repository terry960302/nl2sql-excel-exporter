package com.pandaterry.query_microservice.application.dto;

import lombok.Builder;
import lombok.Getter;
import java.util.UUID;

@Getter
@Builder
public class DataSourceInfo {
    private final UUID id;
    private final String name;
    private final String host;
    private final int port;
    private final String database;
    private final String username;
    private final String encryptedPassword;
    private final String type; // mysql, postgresql 등
}