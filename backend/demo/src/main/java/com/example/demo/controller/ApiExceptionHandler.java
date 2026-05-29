package com.example.demo.controller;

import com.example.demo.exception.MlServiceException;
import com.example.demo.exception.ResourceNotFoundException;
import jakarta.validation.ConstraintViolationException;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ApiExceptionHandler {
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidation(MethodArgumentNotValidException exception) {
        Map<String, String> errors = new LinkedHashMap<>();
        for (FieldError error : exception.getBindingResult().getFieldErrors()) {
            errors.put(error.getField(), error.getDefaultMessage());
        }
        return build(HttpStatus.BAD_REQUEST, "Validasi request gagal.", errors);
    }

    @ExceptionHandler({IllegalArgumentException.class, ConstraintViolationException.class})
    public ResponseEntity<Map<String, Object>> handleBadRequest(RuntimeException exception) {
        return build(HttpStatus.BAD_REQUEST, exception.getMessage(), null);
    }

    @ExceptionHandler(MlServiceException.class)
    public ResponseEntity<Map<String, Object>> handleMlError(MlServiceException exception) {
        return build(HttpStatus.BAD_GATEWAY, exception.getMessage(), null);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleNotFound(ResourceNotFoundException exception) {
        return build(HttpStatus.NOT_FOUND, exception.getMessage(), null);
    }

    private ResponseEntity<Map<String, Object>> build(
        HttpStatus status,
        String message,
        Map<String, String> errors
    ) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", status.value());
        body.put("message", message);
        if (errors != null && !errors.isEmpty()) {
            body.put("errors", errors);
        }
        return ResponseEntity.status(status).body(body);
    }
}
