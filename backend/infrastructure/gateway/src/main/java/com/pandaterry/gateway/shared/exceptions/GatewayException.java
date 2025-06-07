package com.pandaterry.gateway.shared.exceptions;

public class GatewayException extends RuntimeException{
    private ErrorCode errorCode;

    public GatewayException(final ErrorCode errorCode){
        this.errorCode = errorCode;
    }
}
