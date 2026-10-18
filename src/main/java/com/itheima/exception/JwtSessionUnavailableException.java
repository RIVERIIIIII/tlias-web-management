package com.itheima.exception;

public class JwtSessionUnavailableException extends RuntimeException {
    public JwtSessionUnavailableException(String message, Throwable cause) {
        super(message, cause);
    }
}
