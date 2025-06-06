package com.pandaterry.application.exception;

import com.pandaterry.domain.enums.ErrorCode;
import lombok.Getter;

@Getter
public class AgentException extends RuntimeException {
    private final ErrorCode errorCode;

    public AgentException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

    public AgentException(ErrorCode errorCode, Throwable cause) {
        super(errorCode.getMessage(), cause);
        this.errorCode = errorCode;
    }
}