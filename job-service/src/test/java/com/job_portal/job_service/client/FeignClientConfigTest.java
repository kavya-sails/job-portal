package com.job_portal.job_service.client;

import com.job_portal.job_service.exception.UserNotFoundException;
import feign.Request;
import feign.Response;
import feign.codec.ErrorDecoder;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

class FeignClientConfigTest {

    private final FeignClientConfig config = new FeignClientConfig();
    private final ErrorDecoder decoder = config.errorDecoder();

    @Test
    void decode_returnsUserNotFoundException_for404Status() {
        Response response = Mockito.mock(Response.class);
        when(response.status()).thenReturn(404);

        // Provide a Request so Feign's default decoder won't NPE when it inspects the request
        Request req = Request.create(Request.HttpMethod.GET, "/users/1",
                Collections.emptyMap(), null, StandardCharsets.UTF_8);
        when(response.request()).thenReturn(req);

        Exception ex = decoder.decode("UserClient#checkUserExists", response);

        assertNotNull(ex, "Decoder should return an exception object");
        assertTrue(ex instanceof UserNotFoundException, "Expected UserNotFoundException for 404");
        assertEquals("User doesn't exist", ex.getMessage());
    }

    @Test
    void decode_delegatesToDefaultDecoder_forNon404Status() {
        Response response = Mockito.mock(Response.class);
        when(response.status()).thenReturn(500);

        // Provide a Request so Feign's default decoder won't NPE
        Request req = Request.create(Request.HttpMethod.GET, "/users/1",
                Collections.emptyMap(), null, StandardCharsets.UTF_8);
        when(response.request()).thenReturn(req);

        Exception ex = decoder.decode("UserClient#checkUserExists", response);

        assertNotNull(ex, "Decoder should return an exception object");
        assertFalse(ex instanceof UserNotFoundException, "Non-404 should not return UserNotFoundException");
    }
}