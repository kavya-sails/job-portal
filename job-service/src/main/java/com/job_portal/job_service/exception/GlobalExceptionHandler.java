//package com.job_portal.job_service.exception;
//
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.MethodArgumentNotValidException;
//import org.springframework.web.bind.annotation.ExceptionHandler;
//import org.springframework.web.bind.annotation.RestController;
//import org.springframework.web.bind.annotation.RestControllerAdvice;
//import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
//
//import java.time.Instant;
//import java.util.HashMap;
//import java.util.Map;
//@RestControllerAdvice
//
//public class GlobalExceptionHandler {
//    @ExceptionHandler(JobNotFoundException.class)
//    public ResponseEntity<?> handleNotFound(JobNotFoundException ex) {
//        return buildResponse(HttpStatus.NOT_FOUND, ex.getMessage());
//    }
//
//    @ExceptionHandler(ApplicationConflictException.class)
//    public ResponseEntity<?> handleConflict(ApplicationConflictException ex) {
//        return buildResponse(HttpStatus.CONFLICT, ex.getMessage());
//    }
//
//    @ExceptionHandler(MethodArgumentNotValidException.class)
//    public ResponseEntity<?> handleValidation(MethodArgumentNotValidException ex) {
//        String msg = ex.getBindingResult().getFieldErrors()
//                .stream()
//                .map(e -> e.getField() + ": " + e.getDefaultMessage())
//                .reduce((a,b)-> a + "; " + b)
//                .orElse("Validation error");
//        return buildResponse(HttpStatus.BAD_REQUEST, msg);
//    }
//
//    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
//    public ResponseEntity<?> handleTypeMismatch(MethodArgumentTypeMismatchException ex) {
//        return buildResponse(HttpStatus.BAD_REQUEST, "Invalid parameter: " + ex.getName());
//    }
//
//    @ExceptionHandler(Exception.class)
//    public ResponseEntity<?> handleOther(Exception ex) {
//        return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Internal error: " + ex.getMessage());
//    }
//
//    private ResponseEntity<Map<String, Object>> buildResponse(HttpStatus status, String message) {
//        Map<String, Object> body = new HashMap<>();
//        body.put("timestamp", Instant.now().toString());
//        body.put("status", status.value());
//        body.put("error", status.getReasonPhrase());
//        body.put("message", message);
//        return new ResponseEntity<>(body, status);
//    }
//}
package com.job_portal.job_service.exception;

import com.job_portal.job_service.exception.dto.ApiErrorResponse;
import com.job_portal.job_service.exception.Enum.ErrorCode;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.Instant;
import java.util.Map;
import java.util.stream.Collectors;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    private ApiErrorResponse buildResponse(ErrorCode code, String message, String description, String path, Map<String, String> fieldErrors) {
        return ApiErrorResponse.builder()
                .timestamp(Instant.now())
                .code(code.getCode())
                .message(message)
                .description(description)
                .path(path)
                .httpStatus(code.getHttpStatus().value())
                .errors(fieldErrors)
                .build();
    }
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorResponse> handleValidation(MethodArgumentNotValidException ex, HttpServletRequest req) {
        Map<String, String> errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .collect(Collectors.toMap(
                        FieldError::getField,
                        FieldError::getDefaultMessage,
                        (existing, replacement) -> existing // keep first if duplicate
                ));

        ApiErrorResponse body = ApiErrorResponse.builder()
                .timestamp(Instant.now())
                .code(ErrorCode.VALIDATION_FAILED.getCode())
                .message("Validation failed for request")
                .description("One or more fields are invalid")
                .path(req.getRequestURI())
                .httpStatus(ErrorCode.VALIDATION_FAILED.getHttpStatus().value())
                .errors(errors)
                .build();

        return new ResponseEntity<>(body, ErrorCode.VALIDATION_FAILED.getHttpStatus());
    }

    @ExceptionHandler(MissingRequestHeaderException.class)
    public ResponseEntity<ApiErrorResponse> handleMissingHeader(MissingRequestHeaderException ex, HttpServletRequest req) {
        String message = "Missing request header: " + ex.getHeaderName();
        ApiErrorResponse body = buildResponse(ErrorCode.BAD_REQUEST, message, ex.getMessage(), req.getRequestURI(), null);
        return new ResponseEntity<>(body, ErrorCode.BAD_REQUEST.getHttpStatus());
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiErrorResponse> handleNotReadable(HttpMessageNotReadableException ex, HttpServletRequest req) {
        ApiErrorResponse body = buildResponse(ErrorCode.BAD_REQUEST,
                "Malformed JSON request",
                ex.getMostSpecificCause() == null ? ex.getMessage() : ex.getMostSpecificCause().getMessage(),
                req.getRequestURI(),
                null);
        return new ResponseEntity<>(body, ErrorCode.BAD_REQUEST.getHttpStatus());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponse> handleAllUnhandled(Exception ex, HttpServletRequest req) {
        log.error("Unhandled exception at {}: {}", req.getRequestURI(), ex.getMessage(), ex);
        ApiErrorResponse body = buildResponse(ErrorCode.INTERNAL_ERROR,
                "An unexpected error occurred",
                ex.getMessage(),
                req.getRequestURI(),
                null);
        return new ResponseEntity<>(body, ErrorCode.INTERNAL_ERROR.getHttpStatus());
    }
}
