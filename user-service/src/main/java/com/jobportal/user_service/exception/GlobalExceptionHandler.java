package com.jobportal.user_service.exception;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
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
public class GlobalExceptionHandler {

    // UserProfile Not Found
    @ExceptionHandler(UserProfileNotFoundException.class)
    public ResponseEntity<ExceptionResponse> handleUserProfileNotFound(
            UserProfileNotFoundException ex,
            HttpServletRequest req
    ) {
        return buildError(HttpStatus.NOT_FOUND, "User Profile Not Found", ex.getMessage(), req);
    }

    // Auth User Not Found
    @ExceptionHandler(UserNotFound.class)
    public ResponseEntity<ExceptionResponse> handleUserNotFound(
            UserNotFound ex,
            HttpServletRequest req
    ) {
        return buildError(HttpStatus.NOT_FOUND, "User Not Found", ex.getMessage(), req);
    }

    // Forbidden
    @ExceptionHandler(ForbiddenException.class)
    public ResponseEntity<ExceptionResponse> handleForbidden(
            ForbiddenException ex,
            HttpServletRequest req
    ) {
        return buildError(HttpStatus.FORBIDDEN, "Forbidden", ex.getMessage(), req);
    }

    // Data Integrity
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ExceptionResponse> handleCustomDataIntegrityViolation(
            DataIntegrityViolationException ex,
            HttpServletRequest req
    ) {
        return buildError(HttpStatus.CONFLICT, "Conflict", ex.getMessage(), req);
    }

    // @Valid errors
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ExceptionResponse> handleValidationErrors(
            MethodArgumentNotValidException ex,
            HttpServletRequest req
    ) {
        String msg = ex.getBindingResult().getFieldErrors()
                .stream()
                .map(this::formatFieldError)
                .collect(Collectors.joining(", "));

        return buildError(HttpStatus.BAD_REQUEST, "Validation Error", msg, req);
    }

    private String formatFieldError(FieldError fe) {
        return fe.getField() + ": " + fe.getDefaultMessage();
    }

    // Constraint Violations
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ExceptionResponse> handleConstraintViolation(
            ConstraintViolationException ex,
            HttpServletRequest req
    ) {
        String msg = ex.getConstraintViolations().stream()
                .map(cv -> cv.getPropertyPath() + ": " + cv.getMessage())
                .collect(Collectors.joining(", "));

        return buildError(HttpStatus.BAD_REQUEST, "Validation Error", msg, req);
    }

    // JSON Parsing / Enum / Date / Type errors
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ExceptionResponse> handleHttpMessageNotReadable(
            HttpMessageNotReadableException ex,
            HttpServletRequest req
    ) {
        String message;
        Throwable cause = ex.getMostSpecificCause();

        if (cause instanceof InvalidFormatException ife) {
            String fieldPath = ife.getPath().stream()
                    .map(ref -> ref.getFieldName() != null ? ref.getFieldName() : "[" + ref.getIndex() + "]")
                    .collect(Collectors.joining("."));

            String invalidValue = String.valueOf(ife.getValue());
            Class<?> targetType = ife.getTargetType();

            if (targetType.isEnum()) {
                String allowed = Arrays.stream(targetType.getEnumConstants())
                        .map(Object::toString)
                        .collect(Collectors.joining(", "));
                message = String.format("Invalid value '%s' for '%s'. Allowed: %s", invalidValue, fieldPath, allowed);

            } else if (targetType.equals(LocalDate.class)) {
                message = String.format("Invalid date for '%s'. Use YYYY-MM-DD", fieldPath);

            } else if (Number.class.isAssignableFrom(targetType) || targetType.isPrimitive()) {
                message = String.format("Invalid number '%s' for '%s'. Expected %s",
                        invalidValue, fieldPath, targetType.getSimpleName());

            } else {
                message = String.format("Invalid value '%s' for '%s'. Expected %s",
                        invalidValue, fieldPath, targetType.getSimpleName());
            }

        } else if (cause instanceof JsonParseException jpe) {
            message = "Malformed JSON: " + jpe.getOriginalMessage();

        } else if (cause instanceof JsonMappingException jme) {
            String fieldPath = jme.getPath().stream()
                    .map(ref -> ref.getFieldName() != null ? ref.getFieldName() : "[" + ref.getIndex() + "]")
                    .collect(Collectors.joining("."));
            message = String.format("JSON Mapping error in '%s': %s", fieldPath, jme.getOriginalMessage());

        } else {
            message = "Malformed JSON request.";
        }

        return buildError(HttpStatus.BAD_REQUEST, "Invalid Request Body", message, req);
    }

    // Wrong type for query/path params
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ExceptionResponse> handleTypeMismatch(
            MethodArgumentTypeMismatchException ex,
            HttpServletRequest req
    ) {
        String message = String.format(
                "Parameter '%s' must be of type %s",
                ex.getName(),
                ex.getRequiredType() != null ? ex.getRequiredType().getSimpleName() : "unknown"
        );
        return buildError(HttpStatus.BAD_REQUEST, "Type Mismatch", message, req);
    }

    // fallback
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ExceptionResponse> handleAll(
            Exception ex,
            HttpServletRequest req
    ) {
        ex.printStackTrace();
        return buildError(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error", ex.getMessage(), req);
    }

    // Helper
    private ResponseEntity<ExceptionResponse> buildError(
            HttpStatus status,
            String error,
            String message,
            HttpServletRequest req
    ) {
        ExceptionResponse body = new ExceptionResponse(
                LocalDateTime.now(),
                status.value(),
                error,
                message,
                req.getRequestURI()
        );
        return ResponseEntity.status(status).body(body);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ExceptionResponse> handleBadCredentials(
            BadCredentialsException ex,
            HttpServletRequest req
    ) {
        return buildError(
                HttpStatus.UNAUTHORIZED,
                "Unauthorized",
                "Invalid email or password",
                req
        );
    }
}