package com.job_portal.job_service.exception;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.annotation.*;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class GlobalExceptionHandlerTest {

    private MockMvc mockMvc;

    @BeforeEach
    void setup() {
        // create a small controller that throws exceptions to exercise the handlers
        Object controller = new TestExceptionThrowingController();
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void handleJobNotFound_shouldReturn404() throws Exception {
        mockMvc.perform(get("/test/jobNotFound"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Job Not Found"))
                .andExpect(jsonPath("$.message", containsString("Job not found with ID")));
    }

    @Test
    void handleApplicationNotFound_shouldReturn404() throws Exception {
        mockMvc.perform(get("/test/appNotFound"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Application Not Found"));
    }

    @Test
    void handleConflict_shouldReturn409() throws Exception {
        mockMvc.perform(get("/test/conflict"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.error").value("Conflict"));
    }

    @Test
    void handleMissingHeader_shouldReturn400() throws Exception {
        mockMvc.perform(get("/test/missingHeader"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Missing Required Header"));
    }

    @Test
    void handleInvalidJson_shouldReturn400() throws Exception {
        // simulate HttpMessageNotReadableException mapping call endpoint that throws that exact exception
        mockMvc.perform(post("/test/invalidJson")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{ this is not : valid json }"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Malformed JSON"));
    }

    @Test
    void handleForbidden_shouldReturn403() throws Exception {
        mockMvc.perform(get("/test/forbidden"))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.status").value(403))
                .andExpect(jsonPath("$.error").value("Forbidden"));
    }

    @Test
    void handleMessagePublish_shouldReturn500() throws Exception {
        mockMvc.perform(get("/test/messagePublish"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.status").value(500))
                .andExpect(jsonPath("$.error").value("Message Publish Failed"));
    }

    @Test
    void handleGeneric_shouldReturn500() throws Exception {
        mockMvc.perform(get("/test/generic"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.status").value(500))
                .andExpect(jsonPath("$.error").value("Internal Server Error"));
    }

    // tiny test controller to throw exceptions the advice handles
    @RestController
    static class TestExceptionThrowingController {

        @GetMapping("/test/jobNotFound")
        public void jobNotFound() {
            throw new JobNotFoundException(123L);
        }

        @GetMapping("/test/appNotFound")
        public void appNotFound() {
            throw new ApplicationNotFoundException(55L);
        }

        @GetMapping("/test/conflict")
        public void conflict() {
            throw new ApplicationConflictException(1L, 2L);
        }

        @GetMapping("/test/missingHeader")
        public void missingHeader() {
            throw new MissingUserIdHeaderException();
        }

        @PostMapping("/test/invalidJson")
        public void invalidJson() {
            throw new HttpMessageNotReadableException("bad json", new RuntimeException("cause"));
        }

        @GetMapping("/test/forbidden")
        public void forbidden() {
            throw new ForbiddenException("nope");
        }

        @GetMapping("/test/messagePublish")
        public void messagePublish() {
            throw new MessagePublishException("failed to send");
        }

        @GetMapping("/test/generic")
        public void generic() {
            throw new RuntimeException("boom");
        }
    }
}