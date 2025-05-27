package com.pandaterry.schema_microservice.domain.exception;

import lombok.Getter;

@Getter
public class SchemaException extends RuntimeException {
    private final ErrorCode errorCode;

    public SchemaException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

    public SchemaException(ErrorCode errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    public SchemaException(ErrorCode errorCode, Throwable cause) {
        super(errorCode.getMessage(), cause);
        this.errorCode = errorCode;
    }
}