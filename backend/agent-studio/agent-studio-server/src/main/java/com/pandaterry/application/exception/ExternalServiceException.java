package com.pandaterry.application.exception;

import com.pandaterry.domain.enums.ErrorCode;
import lombok.Getter;

@Getter
public class ExternalServiceException extends RuntimeException {
    private final ErrorCode errorCode;
    private final String serviceName;

    public ExternalServiceException(ErrorCode errorCode, String serviceName) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
        this.serviceName = serviceName;
    }

    public ExternalServiceException(ErrorCode errorCode, String serviceName, Throwable cause) {
        super(errorCode.getMessage(), cause);
        this.errorCode = errorCode;
        this.serviceName = serviceName;
    }
}