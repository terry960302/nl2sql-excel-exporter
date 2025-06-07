package com.pandaterry.quota_microservice.domain.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {
    ORGANIZATION_NOT_FOUND(404, "Q001", "Organization not found"),
    PLAN_NOT_FOUND(404, "Q002", "Plan not found");

    private final int status;
    private final String code;
    private final String message;
}
