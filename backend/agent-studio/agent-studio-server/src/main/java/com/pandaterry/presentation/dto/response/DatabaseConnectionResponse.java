package com.pandaterry.presentation.dto.response;

import com.pandaterry.domain.enums.ConnectionStatus;
import java.util.UUID;

public record DatabaseConnectionResponse(
        UUID datasourceId,
        String jdbcUrl,
        ConnectionStatus status) {
}