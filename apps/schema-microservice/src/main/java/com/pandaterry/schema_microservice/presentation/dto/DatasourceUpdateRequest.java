package com.pandaterry.schema_microservice.presentation.dto;

import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DatasourceUpdateRequest{
        private String name;
        private String endpoint;
        private String username;
        private String password;
        private boolean sslEnabled;
        private Map<String, Object> options;

        public static DatasourceUpdateRequest of(String name, String endpoint, String username, String password, boolean sslEnabled, Map<String, Object> options) {
                return DatasourceUpdateRequest.builder()
                        .name(name)
                        .endpoint(endpoint)
                        .username(username)
                        .password(password)
                        .sslEnabled(sslEnabled)
                        .options(options)
                        .build();
        }
}