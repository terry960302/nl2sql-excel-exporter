package com.pandaterry.application.dto.response;

public record ErrorResponse(
        String code,
        String message) {
}