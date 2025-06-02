package com.pandaterry.application.dto.request;

import com.pandaterry.domain.model.database.DatabaseType;

public record DatabaseConnectionRequest(
        String jdbcUrl,
        String username,
        String password,
        DatabaseType type) {
}