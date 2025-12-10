package com.jobportal.user_service.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void handleUserProfileNotFound_shouldReturn404() {
        UserProfileNotFoundException ex =
                new UserProfileNotFoundException("User not found");

        HttpServletRequest req = mock(HttpServletRequest.class);
        when(req.getRequestURI()).thenReturn("/test/1");

        ResponseEntity<ExceptionResponse> response =
                handler.handleUserProfileNotFound(ex, req);

        assertThat(response.getStatusCode().value()).isEqualTo(404);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getError()).isEqualTo("User Profile Not Found");
        assertThat(response.getBody().getMessage()).isEqualTo("User not found");
        assertThat(response.getBody().getPath()).isEqualTo("/test/1");
        assertThat(response.getBody().getTimeStamp())
                .isBeforeOrEqualTo(LocalDateTime.now());
    }

    @Test
    void handleForbidden_shouldReturn403() {
        ForbiddenException ex =
                new ForbiddenException("You are not allowed to do this");

        HttpServletRequest req = mock(HttpServletRequest.class);
        when(req.getRequestURI()).thenReturn("/test/1");

        ResponseEntity<ExceptionResponse> response =
                handler.handleForbidden(ex, req);

        assertThat(response.getStatusCode().value()).isEqualTo(403);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getError()).isEqualTo("Forbidden");
        assertThat(response.getBody().getMessage())
                .isEqualTo("You are not allowed to do this");
        assertThat(response.getBody().getPath()).isEqualTo("/test/1");
    }

    @Test
    void handleValidationErrors_shouldReturn400_withCombinedMessage() {
        Object target = new Object();
        BeanPropertyBindingResult bindingResult =
                new BeanPropertyBindingResult(target, "target");

        bindingResult.addError(
                new FieldError("target", "firstName", "First name is required"));
        bindingResult.addError(
                new FieldError("target", "phone", "Phone number invalid"));

        MethodArgumentNotValidException ex =
                mock(MethodArgumentNotValidException.class);
        when(ex.getBindingResult()).thenReturn(bindingResult);

        HttpServletRequest req = mock(HttpServletRequest.class);
        when(req.getRequestURI()).thenReturn("/test/create");

        ResponseEntity<ExceptionResponse> response =
                handler.handleValidationErrors(ex, req);

        assertThat(response.getStatusCode().value()).isEqualTo(400);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getError())
                .isEqualTo("Validation Error");
        assertThat(response.getBody().getMessage())
                .contains("First name is required")
                .contains("Phone number invalid");
        assertThat(response.getBody().getPath())
                .isEqualTo("/test/create");
    }

    @Test
    void handleCustomDataIntegrityViolation_shouldReturn409() {
        DataIntegrityViolationException ex =
                new DataIntegrityViolationException("Conflict message");

        HttpServletRequest req = mock(HttpServletRequest.class);
        when(req.getRequestURI()).thenReturn("/test/create");

        ResponseEntity<ExceptionResponse> response =
                handler.handleCustomDataIntegrityViolation(ex, req);

        assertThat(response.getStatusCode().value()).isEqualTo(409);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getError()).isEqualTo("Conflict");
        assertThat(response.getBody().getMessage())
                .isEqualTo("Conflict message");
        assertThat(response.getBody().getPath())
                .isEqualTo("/test/create");
    }
}
