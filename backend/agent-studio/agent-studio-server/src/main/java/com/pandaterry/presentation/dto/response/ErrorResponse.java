package com.pandaterry.presentation.dto.response;

public record ErrorResponse(
        String code,
        String message) {
}