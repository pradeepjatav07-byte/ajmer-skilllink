package com.nexoraa.skilllink.api;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;
import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;

@RestControllerAdvice
public class ApiExceptionHandler {
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail validation(MethodArgumentNotValidException ex, HttpServletRequest request) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, "One or more fields need attention.");
        problem.setTitle("Validation failed"); problem.setProperty("code", "VALIDATION_ERROR");
        problem.setProperty("timestamp", Instant.now()); problem.setProperty("path", request.getRequestURI());
        Map<String,String> fields = new LinkedHashMap<>();
        for (FieldError error : ex.getBindingResult().getFieldErrors()) fields.putIfAbsent(error.getField(), error.getDefaultMessage());
        problem.setProperty("fields", fields);
        return problem;
    }

    @ExceptionHandler(AuthenticationException.class)
    public ProblemDetail unauthenticated(AuthenticationException ex, HttpServletRequest request) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.UNAUTHORIZED, "Invalid email or password.");
        problem.setTitle("Authentication failed"); problem.setProperty("code", "AUTHENTICATION_FAILED");
        problem.setProperty("timestamp", Instant.now()); problem.setProperty("path", request.getRequestURI());
        return problem;
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ProblemDetail status(ResponseStatusException ex, HttpServletRequest request) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(ex.getStatusCode(), ex.getReason() == null ? "Request could not be completed." : ex.getReason());
        problem.setTitle("Request error"); problem.setProperty("code", "REQUEST_ERROR");
        problem.setProperty("timestamp", Instant.now()); problem.setProperty("path", request.getRequestURI());
        return problem;
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ProblemDetail forbidden(AccessDeniedException ex, HttpServletRequest request) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.FORBIDDEN, "You do not have permission to perform this action.");
        problem.setTitle("Access denied"); problem.setProperty("code", "FORBIDDEN");
        problem.setProperty("timestamp", Instant.now()); problem.setProperty("path", request.getRequestURI());
        return problem;
    }

    @ExceptionHandler(Exception.class)
    public ProblemDetail unexpected(Exception ex, HttpServletRequest request) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected error occurred.");
        problem.setTitle("Internal server error"); problem.setProperty("code", "INTERNAL_ERROR");
        problem.setProperty("timestamp", Instant.now()); problem.setProperty("path", request.getRequestURI());
        return problem;
    }
}
