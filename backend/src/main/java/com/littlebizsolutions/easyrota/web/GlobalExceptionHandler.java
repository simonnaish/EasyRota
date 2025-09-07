// src/main/java/.../web/GlobalExceptionHandler.java
package com.littlebizsolutions.easyrota.web;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import jakarta.servlet.http.HttpServletRequest;
import java.time.OffsetDateTime;
import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler {

    record FieldViolation(String field, String message, String code) {}
    record ErrorResponse(
            OffsetDateTime timestamp,
            int status,
            String error,
            String message,
            String path,
            List<FieldViolation> errors
    ) {}

    @org.springframework.web.bind.annotation.ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException ex,
                                                          HttpServletRequest request) {
        var violations = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(fe -> new FieldViolation(
                        fe.getField(),
                        fe.getDefaultMessage(),       // already resolved via MessageSource
                        fe.getCode()                  // e.g., "Size","NotBlank"
                ))
                .toList();

        var resp = new ErrorResponse(
                OffsetDateTime.now(),
                HttpStatus.BAD_REQUEST.value(),
                "Bad Request",
                "Validation failed",
                request.getRequestURI(),
                violations
        );

        return ResponseEntity.badRequest().body(resp);
    }

    // Optional: handle @Validated on query/path params
    @org.springframework.web.bind.annotation.ExceptionHandler(jakarta.validation.ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> handleConstraintViolation(
            jakarta.validation.ConstraintViolationException ex, HttpServletRequest request) {

        var violations = ex.getConstraintViolations().stream()
                .map(cv -> new FieldViolation(
                        cv.getPropertyPath().toString(),
                        cv.getMessage(),
                        cv.getConstraintDescriptor().getAnnotation().annotationType().getSimpleName()
                ))
                .toList();

        var resp = new ErrorResponse(
                OffsetDateTime.now(),
                HttpStatus.BAD_REQUEST.value(),
                "Bad Request",
                "Validation failed",
                request.getRequestURI(),
                violations
        );
        return ResponseEntity.badRequest().body(resp);
    }
}
