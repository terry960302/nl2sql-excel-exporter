package com.pandaterry.query_microservice.infrastructure.model;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class DataSourceInfo {
    private final String url;
    private final String username;
    private final String password;
    private final String driverClassName;
}