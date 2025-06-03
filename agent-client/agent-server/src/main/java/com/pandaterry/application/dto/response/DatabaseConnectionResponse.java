package com.pandaterry.application.dto.response;

import com.pandaterry.domain.enums.ConnectionStatus;
import java.util.UUID;

public record DatabaseConnectionResponse(
        UUID id,
        String jdbcUrl,
        ConnectionStatus status) {
}