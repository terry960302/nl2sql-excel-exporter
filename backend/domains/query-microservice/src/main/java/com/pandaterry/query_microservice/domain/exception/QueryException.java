package com.pandaterry.query_microservice.domain.exception;

import com.pandaterry.query_microservice.domain.enums.ErrorCode;
import lombok.Getter;

@Getter
public class QueryException extends RuntimeException {
    private final ErrorCode errorCode;

    public QueryException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }
}