package com.jobportal.user_service.exception;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Path;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.messaging.Message;
import org.springframework.messaging.handler.annotation.support.MethodArgumentNotValidException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler handler;
    private HttpServletRequest request;

    @BeforeEach
    void setup() {
        handler = new GlobalExceptionHandler();
        request = mock(HttpServletRequest.class);
        when(request.getRequestURI()).thenReturn("/api/test");
        when(request.getMethod()).thenReturn("POST");
    }

    // ----------------- Not Found --------------------
    @Test
    void testHandleUserNotFound() {
        ResponseEntity<ExceptionResponse> resp =
                handler.handleNotFound(new UserNotFound("user missing"), request);

        assertEquals(404, resp.getStatusCodeValue());
        assertEquals("Not Found", resp.getBody().getError());
        assertEquals("user missing", resp.getBody().getMessage());
    }

    // ----------------- Forbidden --------------------
    @Test
    void testHandleForbidden() {
        ResponseEntity<ExceptionResponse> resp =
                handler.handleForbidden(new ForbiddenException("denied"), request);

        assertEquals(403, resp.getStatusCodeValue());
        assertEquals("Forbidden", resp.getBody().getError());
    }

    // ----------------- Bad Credentials --------------
    @Test
    void testHandleBadCredentials() {
        ResponseEntity<ExceptionResponse> resp =
                handler.handleBadCredentials(new BadCredentialsException("bad"), request);

        assertEquals(401, resp.getStatusCodeValue());
        assertEquals("Invalid email or password", resp.getBody().getMessage());
    }



    // ----------------- MethodArgumentNotValid --------
    @Test
    void testMethodArgumentNotValid() {

        // mock BindingResult
        BindingResult br = mock(BindingResult.class);

        when(br.getFieldErrors()).thenReturn(
                java.util.List.of(
                        new FieldError("obj", "firstName", "First required"),
                        new FieldError("obj", "phone", "Phone invalid")
                )
        );

        // mock the exception itself
        MethodArgumentNotValidException ex = mock(MethodArgumentNotValidException.class);

        // Stub getBindingResult()
        when(ex.getBindingResult()).thenReturn(br);

        ResponseEntity<ExceptionResponse> resp =
                handler.handleMethodArgNotValid(ex, request);

        assertEquals(400, resp.getStatusCodeValue());
        assertTrue(resp.getBody().getMessage().contains("firstName: First required"));
        assertTrue(resp.getBody().getMessage().contains("phone: Phone invalid"));
    }
    @Test
    void testConstraintViolation() {
        // create a mock ConstraintViolation and a mock Path (toString will be used)
        @SuppressWarnings("unchecked")
        ConstraintViolation<Object> cv = mock(ConstraintViolation.class);
        Path path = mock(Path.class);

        when(path.toString()).thenReturn("email");         // property path string
        when(cv.getPropertyPath()).thenReturn(path);       // return Path, not String
        when(cv.getMessage()).thenReturn("must be valid");

        ConstraintViolationException ex = new ConstraintViolationException(Set.of(cv));

        ResponseEntity<ExceptionResponse> resp =
                handler.handleConstraintViolation(ex, request);

        assertEquals(400, resp.getStatusCodeValue());
        assertTrue(resp.getBody().getMessage().contains("email: must be valid"));
    }

    // ---------------- HttpMessageNotReadable ----------
    @Test
    void testHttpMessageNotReadable_default() {
        Exception cause = new RuntimeException("invalid json");
        HttpMessageNotReadableException ex =
                new HttpMessageNotReadableException("bad json", cause);

        ResponseEntity<ExceptionResponse> resp =
                handler.handleHttpMessageNotReadable(ex, request);

        assertEquals(400, resp.getStatusCodeValue());
        assertEquals("Malformed JSON request.", resp.getBody().getMessage());
    }

    // ---------------- Type Mismatch -------------------
    @Test
    void testTypeMismatch() {
        MethodArgumentTypeMismatchException ex =
                new MethodArgumentTypeMismatchException("abc", Integer.class, "id", null, new IllegalArgumentException());

        ResponseEntity<ExceptionResponse> resp =
                handler.handleTypeMismatch(ex, request);

        assertEquals(400, resp.getStatusCodeValue());
        assertTrue(resp.getBody().getMessage().contains("Parameter 'id' must be of type Integer"));
    }

    // ---------------- Internal Server Error -----------
    @Test
    void testHandleAll() {
        Exception ex = new RuntimeException("boom");

        ResponseEntity<ExceptionResponse> resp =
                handler.handleAll(ex, request);

        assertEquals(500, resp.getStatusCodeValue());
        assertEquals("Internal Server Error", resp.getBody().getError());
        assertEquals("An unexpected error occurred", resp.getBody().getMessage());
        assertNotNull(resp.getBody().getTimeStamp());
    }
}
