package com.jobportal.user_service.exception;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // ---------- 404: UserProfile not found ----------
    @ExceptionHandler(UserProfileNotFoundException.class)
    public ResponseEntity<ExceptionResponse> handleUserProfileNotFound(
            UserProfileNotFoundException ex,
            HttpServletRequest req
    ) {
        ExceptionResponse response = new ExceptionResponse(
                LocalDateTime.now(),
                HttpStatus.NOT_FOUND.value(),
                "User Profile Not Found",
                ex.getMessage(),
                req.getRequestURI()
        );

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    // ---------- 404: Auth user not found ----------
    @ExceptionHandler(UserNotFound.class)
    public ResponseEntity<ExceptionResponse> handleUserNotFound(
            UserNotFound ex,
            HttpServletRequest req
    ) {
        ExceptionResponse response = new ExceptionResponse(
                LocalDateTime.now(),
                HttpStatus.NOT_FOUND.value(),
                "User Not Found",
                ex.getMessage(),
                req.getRequestURI()
        );

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    // ---------- 403: Forbidden (business access check) ----------
    @ExceptionHandler(ForbiddenException.class)
    public ResponseEntity<ExceptionResponse> handleForbidden(
            ForbiddenException ex,
            HttpServletRequest req
    ) {
        ExceptionResponse body = new ExceptionResponse(
                LocalDateTime.now(),
                HttpStatus.FORBIDDEN.value(),
                "Forbidden",
                ex.getMessage(),
                req.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(body);
    }

    // ---------- 400: @Valid @RequestBody (DTO validation) ----------
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ExceptionResponse> handleValidationErrors(
            MethodArgumentNotValidException ex,
            HttpServletRequest request
    ) {
        // collect ALL validation errors (Bean Validation)
        String message = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(fieldError -> formatFieldError(fieldError))
                .collect(Collectors.joining(", "));

        ExceptionResponse response = new ExceptionResponse(
                LocalDateTime.now(),
                HttpStatus.BAD_REQUEST.value(),
                "Validation Error",
                message,
                request.getRequestURI()
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    private String formatFieldError(FieldError fieldError) {
        return fieldError.getField() + ": " + fieldError.getDefaultMessage();
    }

    // ---------- 400: @Validated on params / path variables ----------
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ExceptionResponse> handleConstraintViolation(
            ConstraintViolationException ex,
            HttpServletRequest request
    ) {
        String message = ex.getConstraintViolations()
                .stream()
                .map(cv -> cv.getPropertyPath() + ": " + cv.getMessage())
                .collect(Collectors.joining(", "));

        ExceptionResponse response = new ExceptionResponse(
                LocalDateTime.now(),
                HttpStatus.BAD_REQUEST.value(),
                "Validation Error",
                message,
                request.getRequestURI()
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    // ---------- 400: Malformed JSON / invalid enum / invalid date / wrong type, etc. ----------
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ExceptionResponse> handleHttpMessageNotReadable(
            HttpMessageNotReadableException ex,
            HttpServletRequest request
    ) {
        String message;

        Throwable cause = ex.getMostSpecificCause();

        if (cause instanceof InvalidFormatException ife) {
            Class<?> targetType = ife.getTargetType();

            // Build JSON path: e.g. "education.passOutYear"
            String fieldPath = ife.getPath().stream()
                    .map(ref -> ref.getFieldName() != null
                            ? ref.getFieldName()
                            : "[" + ref.getIndex() + "]")
                    .collect(Collectors.joining("."));

            String invalidValue = String.valueOf(ife.getValue());

            // Case 1: Enum field (JobRole, ExperienceLevel, EducationLevel, Specialisation...)
            if (targetType.isEnum()) {
                String allowedValues = Arrays.stream(targetType.getEnumConstants())
                        .map(Object::toString)
                        .collect(Collectors.joining(", "));

                message = String.format(
                        "Invalid value '%s' for field '%s'. Allowed values are: %s",
                        invalidValue,
                        fieldPath,
                        allowedValues
                );

                // Case 2: LocalDate parsing issue
            } else if (targetType.equals(LocalDate.class)) {
                message = String.format(
                        "Invalid date format for field '%s'. Please use 'YYYY-MM-DD'.",
                        fieldPath
                );

                // Case 3: Number types (Integer, Long, Double, etc.)
            } else if (Number.class.isAssignableFrom(targetType)
                    || targetType.isPrimitive()) {
                message = String.format(
                        "Invalid numeric value '%s' for field '%s'. Please provide a valid %s.",
                        invalidValue,
                        fieldPath,
                        targetType.getSimpleName()
                );

                // Case 4: Boolean
            } else if (targetType.equals(Boolean.class) || targetType.equals(boolean.class)) {
                message = String.format(
                        "Invalid boolean value '%s' for field '%s'. Expected true or false.",
                        invalidValue,
                        fieldPath
                );

                // Case 5: Generic type
            } else {
                message = String.format(
                        "Invalid value '%s' for field '%s'. Expected type: %s.",
                        invalidValue,
                        fieldPath,
                        targetType.getSimpleName()
                );
            }

        } else if (cause instanceof JsonParseException jpe) {
            // Low-level malformed JSON (e.g. missing comma, bad quotes)
            message = "Malformed JSON request: " + jpe.getOriginalMessage();

        } else if (cause instanceof JsonMappingException jme) {
            // More generic mapping problem
            String fieldPath = jme.getPath().stream()
                    .map(ref -> ref.getFieldName() != null
                            ? ref.getFieldName()
                            : "[" + ref.getIndex() + "]")
                    .collect(Collectors.joining("."));

            message = String.format(
                    "JSON mapping error for field '%s': %s",
                    fieldPath,
                    jme.getOriginalMessage()
            );

        } else {
            // Fallback: generic malformed JSON
            message = "Malformed JSON request.";
        }

        ExceptionResponse response = new ExceptionResponse(
                LocalDateTime.now(),
                HttpStatus.BAD_REQUEST.value(),
                "Invalid Request Body",
                message,
                request.getRequestURI()
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    // ---------- 400: Wrong type for query/path param ----------
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ExceptionResponse> handleTypeMismatch(
            MethodArgumentTypeMismatchException ex,
            HttpServletRequest request
    ) {
        String message = String.format(
                "Parameter '%s' must be of type %s",
                ex.getName(),
                ex.getRequiredType() != null ? ex.getRequiredType().getSimpleName() : "unknown"
        );

        ExceptionResponse response = new ExceptionResponse(
                LocalDateTime.now(),
                HttpStatus.BAD_REQUEST.value(),
                "Type Mismatch",
                message,
                request.getRequestURI()
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    // ---------- 409: Data integrity / duplicate profile ----------
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ExceptionResponse> handleCustomDataIntegrityViolation(
            DataIntegrityViolationException ex,
            HttpServletRequest req
    ) {
        ExceptionResponse response = new ExceptionResponse(
                LocalDateTime.now(),
                HttpStatus.CONFLICT.value(),       // 409
                "Conflict",
                ex.getMessage(),
                req.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
    }

    // ---------- 500: Fallback for unexpected errors ----------
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ExceptionResponse> handleAll(
            Exception ex,
            HttpServletRequest request
    ) {
        ex.printStackTrace(); // for debugging

        ExceptionResponse err = new ExceptionResponse(
                LocalDateTime.now(),
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Internal Server Error",
                ex.getMessage(),
                request.getRequestURI()
        );

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(err);
    }
}
