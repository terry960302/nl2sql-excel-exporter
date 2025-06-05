package com.pandaterry.presentation.dto.request;

import com.pandaterry.domain.enums.DatabaseType;

public record DatabaseConnectionRequest(
        String jdbcUrl,
        String username,
        String password,
        DatabaseType type) {
}