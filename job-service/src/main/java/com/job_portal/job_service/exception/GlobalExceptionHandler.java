package com.job_portal.job_service.exception;

import com.job_portal.job_service.dto.query.ExceptionResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.flywaydb.core.api.ErrorDetails;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    private ResponseEntity<ExceptionResponse> build(HttpStatus status, String error, String message, HttpServletRequest req) {
        return new ResponseEntity<>(
                new ExceptionResponse(LocalDateTime.now(), status.value(), error, message, req.getRequestURI()),
                status
        );
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ExceptionResponse> handleUserNotFoundException(UserNotFoundException ex, HttpServletRequest req) {
        return  build(HttpStatus.NOT_FOUND, ex.getMessage(), ex.getMessage(), req);
    }
    @ExceptionHandler(JobNotFoundException.class)
    public ResponseEntity<ExceptionResponse> handleJobNotFound(JobNotFoundException ex, HttpServletRequest req) {
        return build(HttpStatus.NOT_FOUND, "Job Not Found", ex.getMessage(), req);
    }

    @ExceptionHandler(ApplicationNotFoundException.class)
    public ResponseEntity<ExceptionResponse> handleApplicationNotFound(ApplicationNotFoundException ex, HttpServletRequest req) {
        return build(HttpStatus.NOT_FOUND, "Application Not Found", ex.getMessage(), req);
    }

    @ExceptionHandler(ApplicationConflictException.class)
    public ResponseEntity<ExceptionResponse> handleConflict(ApplicationConflictException ex, HttpServletRequest req) {
        return build(HttpStatus.CONFLICT, "Conflict", ex.getMessage(), req);
    }


    @ExceptionHandler(InvalidApplicationStatusException.class)
    public ResponseEntity<ExceptionResponse> handleInvalidStatus(InvalidApplicationStatusException ex, HttpServletRequest req) {
        return build(HttpStatus.BAD_REQUEST, "Invalid Status", ex.getMessage(), req);
    }

    @ExceptionHandler(MissingUserIdHeaderException.class)
    public ResponseEntity<ExceptionResponse> handleMissingHeader(MissingUserIdHeaderException ex, HttpServletRequest req) {
        return build(HttpStatus.BAD_REQUEST, "Missing Required Header", ex.getMessage(), req);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ExceptionResponse> handleInvalidJson(HttpMessageNotReadableException ex, HttpServletRequest req) {
        return build(HttpStatus.BAD_REQUEST, "Malformed JSON", ex.getMostSpecificCause().getMessage(), req);
    }


    @ExceptionHandler(ForbiddenException.class)
    public ResponseEntity<ExceptionResponse> handleForbidden(ForbiddenException ex, HttpServletRequest req) {
        return build(HttpStatus.FORBIDDEN, "Forbidden", ex.getMessage(), req);
    }


    @ExceptionHandler(MessagePublishException.class)
    public ResponseEntity<ExceptionResponse> handleMessagePublish(MessagePublishException ex, HttpServletRequest req) {
        return build(HttpStatus.INTERNAL_SERVER_ERROR, "Message Publish Failed", ex.getMessage(), req);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ExceptionResponse> handleAll(Exception ex, HttpServletRequest req) {
        log.error("Unexpected Exception", ex);
        return build(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error", "Something went wrong", req);
    }
}