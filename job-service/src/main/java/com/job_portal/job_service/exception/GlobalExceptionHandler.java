package com.job_portal.job_service.exception;

import com.job_portal.job_service.dto.query.ExceptionResponce;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    private ResponseEntity<ExceptionResponce> build(HttpStatus status, String error, String message, HttpServletRequest req) {
        return new ResponseEntity<>(
                new ExceptionResponce(LocalDateTime.now(), status.value(), error, message, req.getRequestURI()),
                status
        );
    }

    @ExceptionHandler(JobNotFoundException.class)
    public ResponseEntity<ExceptionResponce> handleJobNotFound(JobNotFoundException ex, HttpServletRequest req) {
        return build(HttpStatus.NOT_FOUND, "Job Not Found", ex.getMessage(), req);
    }

    @ExceptionHandler(ApplicationNotFoundException.class)
    public ResponseEntity<ExceptionResponce> handleApplicationNotFound(ApplicationNotFoundException ex, HttpServletRequest req) {
        return build(HttpStatus.NOT_FOUND, "Application Not Found", ex.getMessage(), req);
    }

    @ExceptionHandler(ApplicationConflictException.class)
    public ResponseEntity<ExceptionResponce> handleConflict(ApplicationConflictException ex, HttpServletRequest req) {
        return build(HttpStatus.CONFLICT, "Conflict", ex.getMessage(), req);
    }


    @ExceptionHandler(InvalidApplicationStatusException.class)
    public ResponseEntity<ExceptionResponce> handleInvalidStatus(InvalidApplicationStatusException ex, HttpServletRequest req) {
        return build(HttpStatus.BAD_REQUEST, "Invalid Status", ex.getMessage(), req);
    }

    @ExceptionHandler(MissingUserIdHeaderException.class)
    public ResponseEntity<ExceptionResponce> handleMissingHeader(MissingUserIdHeaderException ex, HttpServletRequest req) {
        return build(HttpStatus.BAD_REQUEST, "Missing Required Header", ex.getMessage(), req);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ExceptionResponce> handleInvalidJson(HttpMessageNotReadableException ex, HttpServletRequest req) {
        return build(HttpStatus.BAD_REQUEST, "Malformed JSON", ex.getMostSpecificCause().getMessage(), req);
    }


    @ExceptionHandler(ForbiddenException.class)
    public ResponseEntity<ExceptionResponce> handleForbidden(ForbiddenException ex, HttpServletRequest req) {
        return build(HttpStatus.FORBIDDEN, "Forbidden", ex.getMessage(), req);
    }


    @ExceptionHandler(MessagePublishException.class)
    public ResponseEntity<ExceptionResponce> handleMessagePublish(MessagePublishException ex, HttpServletRequest req) {
        return build(HttpStatus.INTERNAL_SERVER_ERROR, "Message Publish Failed", ex.getMessage(), req);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ExceptionResponce> handleAll(Exception ex, HttpServletRequest req) {
        log.error("Unexpected Exception", ex);
        return build(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error", "Something went wrong", req);
    }
}
