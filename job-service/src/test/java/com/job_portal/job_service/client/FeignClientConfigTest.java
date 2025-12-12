package com.job_portal.job_service.client;

import com.job_portal.job_service.exception.UserNotFoundException;
import feign.Response;
import feign.codec.ErrorDecoder;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

class FeignClientConfigTest {

    private final FeignClientConfig config = new FeignClientConfig();
    private final ErrorDecoder decoder = config.errorDecoder();

    @Test
    void decode_returnsUserNotFoundException_for404Status() {
        // Arrange
        Response response = Mockito.mock(Response.class);
        when(response.status()).thenReturn(404);

        // Act
        Exception ex = decoder.decode("UserClient#checkUserExists", response);

        // Assert
        assertNotNull(ex, "Decoder should return an exception object");
        assertTrue(ex instanceof UserNotFoundException, "Expected UserNotFoundException for 404");
        assertEquals("User doesn't exist", ex.getMessage());
    }

    @Test
    void decode_delegatesToDefaultDecoder_forNon404Status() {
        // Arrange
        Response response = Mockito.mock(Response.class);
        when(response.status()).thenReturn(500);

        // Act
        Exception ex = decoder.decode("UserClient#checkUserExists", response);

        // Assert
        assertNotNull(ex, "Decoder should return an exception object");
        assertFalse(ex instanceof UserNotFoundException, "Non-404 should not return UserNotFoundException");
        // Optionally assert the class name starts with feign's default exception class
        // e.g. assertTrue(ex.getClass().getSimpleName().toLowerCase().contains("feign") || ... );
    }
}