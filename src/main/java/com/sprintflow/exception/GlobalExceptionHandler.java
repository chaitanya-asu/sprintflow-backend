package com.sprintflow.exception;

import com.sprintflow.dto.ApiResponseDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.time.LocalDateTime;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);
    
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiResponseDTO<?>> handleResourceNotFoundException(
            ResourceNotFoundException ex, WebRequest request) {
        
        ApiResponseDTO<?> response = ApiResponseDTO.builder()
                .success(false)
                .message(ex.getMessage())
                .statusCode(HttpStatus.NOT_FOUND.value())
                .error("Resource Not Found")
                .timestamp(LocalDateTime.now())
                .path(request.getDescription(false).replace("uri=", ""))
                .build();
        
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }
    
    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ApiResponseDTO<?>> handleAuthenticationException(
            AuthenticationException ex, WebRequest request) {
        
        ApiResponseDTO<?> response = ApiResponseDTO.builder()
                .success(false)
                .message(ex.getMessage())
                .statusCode(HttpStatus.UNAUTHORIZED.value())
                .error("Authentication Failed")
                .timestamp(LocalDateTime.now())
                .path(request.getDescription(false).replace("uri=", ""))
                .build();
        
        return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
    }
    
    @ExceptionHandler(DuplicateResourceException.class)
    public ResponseEntity<ApiResponseDTO<?>> handleDuplicateResourceException(
            DuplicateResourceException ex, WebRequest request) {
        
        ApiResponseDTO<?> response = ApiResponseDTO.builder()
                .success(false)
                .message(ex.getMessage())
                .statusCode(HttpStatus.CONFLICT.value())
                .error("Duplicate Resource")
                .timestamp(LocalDateTime.now())
                .path(request.getDescription(false).replace("uri=", ""))
                .build();
        
        return new ResponseEntity<>(response, HttpStatus.CONFLICT);
    }
    
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponseDTO<?>> handleGlobalException(
            Exception ex, WebRequest request) {

        log.error("Unhandled exception at {}: {}", request.getDescription(false), ex.getMessage(), ex);

        ApiResponseDTO<?> response = ApiResponseDTO.builder()
                .success(false)
                .message("An internal server error occurred")
                .statusCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .error("Internal Server Error")
                .timestamp(LocalDateTime.now())
                .path(request.getDescription(false).replace("uri=", ""))
                .build();
        
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }
    
    @ExceptionHandler(org.springframework.web.bind.MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponseDTO<?>> handleValidationException(
            org.springframework.web.bind.MethodArgumentNotValidException ex, WebRequest request) {
        
        String errors = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .reduce((a, b) -> a + ", " + b)
                .orElse("Validation failed");
        
        ApiResponseDTO<?> response = ApiResponseDTO.builder()
                .success(false)
                .message(errors)
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .error("Validation Error")
                .timestamp(LocalDateTime.now())
                .path(request.getDescription(false).replace("uri=", ""))
                .build();
        
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }
    
    @ExceptionHandler(jakarta.validation.ConstraintViolationException.class)
    public ResponseEntity<ApiResponseDTO<?>> handleConstraintViolation(
            jakarta.validation.ConstraintViolationException ex, WebRequest request) {
        
        String errors = ex.getConstraintViolations().stream()
                .map(violation -> violation.getPropertyPath() + ": " + violation.getMessage())
                .reduce((a, b) -> a + ", " + b)
                .orElse("Constraint violation");
        
        ApiResponseDTO<?> response = ApiResponseDTO.builder()
                .success(false)
                .message(errors)
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .error("Validation Error")
                .timestamp(LocalDateTime.now())
                .path(request.getDescription(false).replace("uri=", ""))
                .build();
        
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }
}
