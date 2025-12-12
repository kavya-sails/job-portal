package com.jobportal.user_service.exception;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.stream.Collectors;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler({UserProfileNotFoundException.class, UserNotFound.class})
    public ResponseEntity<ExceptionResponse> handleNotFound(RuntimeException ex, HttpServletRequest req) {
        return buildError(HttpStatus.NOT_FOUND, "Not Found", ex.getMessage(), req);
    }

    @ExceptionHandler(ForbiddenException.class)
    public ResponseEntity<ExceptionResponse> handleForbidden(ForbiddenException ex, HttpServletRequest req) {
        return buildError(HttpStatus.FORBIDDEN, "Forbidden", ex.getMessage(), req);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ExceptionResponse> handleBadCredentials(BadCredentialsException ex, HttpServletRequest req) {
        return buildError(HttpStatus.UNAUTHORIZED, "Unauthorized", "Invalid email or password", req);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ExceptionResponse> handleMethodArgNotValid(org.springframework.messaging.handler.annotation.support.MethodArgumentNotValidException ex, HttpServletRequest req) {
        String message = ex.getBindingResult().getFieldErrors()
                .stream()
                .map(this::formatFieldError)
                .collect(Collectors.joining(", "));
        return buildError(HttpStatus.BAD_REQUEST, "Validation Error", message, req);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ExceptionResponse> handleConstraintViolation(ConstraintViolationException ex, HttpServletRequest req) {
        String message = ex.getConstraintViolations().stream()
                .map(cv -> cv.getPropertyPath() + ": " + cv.getMessage())
                .collect(Collectors.joining(", "));
        return buildError(HttpStatus.BAD_REQUEST, "Validation Error", message, req);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ExceptionResponse> handleHttpMessageNotReadable(HttpMessageNotReadableException ex, HttpServletRequest req) {
        Throwable cause = ex.getMostSpecificCause();
        String message;
        switch (cause) {
            case InvalidFormatException ife -> message = formatInvalidFormatException(ife);
            case JsonParseException jpe -> message = "Malformed JSON: " + jpe.getOriginalMessage();
            case JsonMappingException jme -> {
                String fieldPath = jme.getPath().stream()
                        .map(ref -> ref.getFieldName() != null ? ref.getFieldName() : "[" + ref.getIndex() + "]")
                        .collect(Collectors.joining("."));
                message = String.format("JSON mapping error in '%s': %s", fieldPath, jme.getOriginalMessage());
            }
            default -> message = "Malformed JSON request.";
        }
        return buildError(HttpStatus.BAD_REQUEST, "Invalid Request Body", message, req);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ExceptionResponse> handleTypeMismatch(MethodArgumentTypeMismatchException ex, HttpServletRequest req) {
        String message = String.format("Parameter '%s' must be of type %s",
                ex.getName(),
                ex.getRequiredType() != null ? ex.getRequiredType().getSimpleName() : "unknown");
        return buildError(HttpStatus.BAD_REQUEST, "Type Mismatch", message, req);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ExceptionResponse> handleAll(Exception ex, HttpServletRequest req) {
        log.error("Unhandled exception at {} {}: {}", req.getMethod(), req.getRequestURI(), ex.getMessage(), ex);
        return buildError(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error", "An unexpected error occurred", req);
    }

    private ResponseEntity<ExceptionResponse> buildError(HttpStatus status, String error, String message, HttpServletRequest req) {
        ExceptionResponse body = new ExceptionResponse(
                LocalDateTime.now(),
                status.value(),
                error,
                message,
                req.getRequestURI()
        );
        return ResponseEntity.status(status).body(body);
    }

    private String formatFieldError(FieldError fe) {
        return fe.getField() + ": " + fe.getDefaultMessage();
    }

    private String formatInvalidFormatException(InvalidFormatException ife) {
        String fieldPath = ife.getPath().stream()
                .map(ref -> ref.getFieldName() != null ? ref.getFieldName() : "[" + ref.getIndex() + "]")
                .collect(Collectors.joining("."));
        String invalidValue = String.valueOf(ife.getValue());
        Class<?> targetType = ife.getTargetType();

        if (targetType.isEnum()) {
            String allowed = Arrays.stream(targetType.getEnumConstants())
                    .map(Object::toString)
                    .collect(Collectors.joining(", "));
            return String.format("Invalid value '%s' for '%s'. Allowed: %s", invalidValue, fieldPath, allowed);
        } else if (targetType.equals(LocalDate.class)) {
            return String.format("Invalid date for '%s'. Use YYYY-MM-DD", fieldPath);
        } else if (Number.class.isAssignableFrom(targetType) || targetType.isPrimitive()) {
            return String.format("Invalid number '%s' for '%s'. Expected %s",
                    invalidValue, fieldPath, targetType.getSimpleName());
        } else {
            return String.format("Invalid value '%s' for '%s'. Expected %s",
                    invalidValue, fieldPath, targetType.getSimpleName());
        }
    }
}