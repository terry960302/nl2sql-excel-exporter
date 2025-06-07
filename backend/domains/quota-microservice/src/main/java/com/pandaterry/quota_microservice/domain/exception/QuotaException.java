package com.pandaterry.quota_microservice.domain.exception;

import lombok.Getter;

@Getter
public class QuotaException extends RuntimeException {
    private final ErrorCode errorCode;

    public QuotaException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }
}
