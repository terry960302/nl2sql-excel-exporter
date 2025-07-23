package com.pandaterry.presentation.dto.response;

import com.pandaterry.domain.enums.ConnectionStatus;
import io.micronaut.serde.annotation.Serdeable;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Serdeable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class DatabaseConnectionResponse {
    private UUID datasourceId;
    private String jdbcUrl;
    private ConnectionStatus status;
}