package com.transitops.backend.exception;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessRuleViolationException.class)
    public ResponseEntity<Map<String, Object>> handleBusinessRuleException(
            BusinessRuleViolationException ex, WebRequest request) {
        return buildErrorResponse(HttpStatus.BAD_REQUEST, "Business Rule Violation", ex.getMessage(), request, null);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleNotFoundException(
            ResourceNotFoundException ex, WebRequest request) {
        return buildErrorResponse(HttpStatus.NOT_FOUND, "Resource Not Found", ex.getMessage(), request, null);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationException(
            MethodArgumentNotValidException ex, WebRequest request) {
        Map<String, String> fieldErrors = new HashMap<>();
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            fieldErrors.put(error.getField(), error.getDefaultMessage());
        }
        return buildErrorResponse(HttpStatus.BAD_REQUEST, "Validation Error", "Invalid request payload", request, fieldErrors);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Map<String, Object>> handleDataIntegrityViolationException(
            DataIntegrityViolationException ex, WebRequest request) {
        String message = "Database integrity constraint violation occurred.";
        if (ex.getRootCause() != null && ex.getRootCause().getMessage().contains("uk_")) {
            message = "A resource already exists with that unique field value.";
        }
        return buildErrorResponse(HttpStatus.CONFLICT, "Resource Conflict", message, request, null);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGlobalException(
            Exception ex, WebRequest request) {
        // Enforce zero exposure of internal code paths or runtime exceptions on HTTP 500
        return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error", 
                "An unexpected server error occurred. Please contact administrative support.", request, null);
    }

    private ResponseEntity<Map<String, Object>> buildErrorResponse(
            HttpStatus status, String error, String message, WebRequest request, Map<String, String> fieldErrors) {
        
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", status.value());
        body.put("error", error);
        body.put("message", message);
        body.put("path", ((ServletWebRequest) request).getRequest().getRequestURI());
        
        if (fieldErrors != null && !fieldErrors.isEmpty()) {
            body.put("fieldErrors", fieldErrors);
        }

        return new ResponseEntity<>(body, status);
    }
}