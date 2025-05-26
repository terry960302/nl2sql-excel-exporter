package com.pandaterry.auth_microservice.application.validator;

import com.pandaterry.auth_microservice.domain.exception.AuthException;
import com.pandaterry.auth_microservice.domain.exception.ErrorCode;

public class PasswordValidator {
    private static final String PASSWORD_PATTERN = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!])(?=\\S+$).{8,}$";

    public static void validate(String password) {
        if (password == null || !password.matches(PASSWORD_PATTERN)) {
            throw new AuthException(ErrorCode.WEAK_PASSWORD);
        }
    }
}