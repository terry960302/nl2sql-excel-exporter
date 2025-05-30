package com.pandaterry.schema_microservice.presentation.exception;

import com.pandaterry.schema_microservice.domain.exception.ErrorCode;
import com.pandaterry.schema_microservice.domain.exception.SchemaException;
import com.pandaterry.schema_microservice.presentation.dto.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler {

        @ExceptionHandler(SchemaException.class)
        public ResponseEntity<ErrorResponse> handleSchemaException(
                        SchemaException ex,
                        HttpServletRequest request) {
                ErrorCode errorCode = ex.getErrorCode();
                return new ResponseEntity<>(
                                ErrorResponse.of(
                                                errorCode.getCode(),
                                                errorCode.getMessage()),
                                HttpStatus.valueOf(errorCode.getStatus()));
        }

        @ExceptionHandler(MethodArgumentNotValidException.class)
        public ResponseEntity<ErrorResponse> handleValidationExceptions(
                        MethodArgumentNotValidException ex,
                        HttpServletRequest request) {

                return ResponseEntity
                                .status(HttpStatus.BAD_REQUEST)
                                .body(ErrorResponse.of(
                                                ErrorCode.INVALID_INPUT.getCode(),
                                                ErrorCode.INVALID_INPUT.getMessage()));
        }

        @ExceptionHandler(ConstraintViolationException.class)
        public ResponseEntity<ErrorResponse> handleConstraintViolationException(
                        ConstraintViolationException ex,
                        HttpServletRequest request) {

                return ResponseEntity
                                .status(HttpStatus.BAD_REQUEST)
                                .body(ErrorResponse.of(
                                                ErrorCode.INVALID_INPUT.getCode(),
                                                ErrorCode.INVALID_INPUT.getMessage()));
        }

        @ExceptionHandler(MissingRequestHeaderException.class)
        public ResponseEntity<ErrorResponse> handleMissingRequestHeaderException(
                MissingRequestHeaderException ex,
                HttpServletRequest request){
                return ResponseEntity
                        .status(HttpStatus.BAD_REQUEST)
                        .body(ErrorResponse.of(
                                ErrorCode.MISSING_HEADER_VALUE.getCode(),
                                ErrorCode.MISSING_HEADER_VALUE.getMessage()
                        ));
        }

        @ExceptionHandler(Exception.class)
        public ResponseEntity<ErrorResponse> handleGlobalException(
                        Exception ex,
                        HttpServletRequest request) {
                return ResponseEntity
                                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                                .body(ErrorResponse.of(
                                                ErrorCode.INTERNAL_SERVER_ERROR.getCode(),
                                                ex.getMessage()));
        }
}