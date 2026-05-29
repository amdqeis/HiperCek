package com.example.demo.exception;

public class MlServiceException extends RuntimeException {
    public MlServiceException(String message, Throwable cause) {
        super(message, cause);
    }

    public MlServiceException(String message) {
        super(message);
    }
}
