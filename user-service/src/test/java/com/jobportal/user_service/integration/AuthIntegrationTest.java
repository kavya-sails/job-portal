package com.jobportal.user_service.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jobportal.user_service.dto.LoginRequest;
import com.jobportal.user_service.dto.RegisterRequest;
import com.jobportal.user_service.enums.RoleName;
import com.jobportal.user_service.repository.UserCredentialRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(properties = "spring.profiles.active=test")
@AutoConfigureMockMvc
@Transactional
@Import(TestBeansConfig.class)
public class AuthIntegrationTest extends IntegrationTestBase {

    @Autowired MockMvc mockMvc;
    @Autowired ObjectMapper objectMapper;
    @Autowired UserCredentialRepository userCredentialRepository;

    @Test
    void register_success() throws Exception {
        RegisterRequest req = new RegisterRequest();
        req.setEmail("user1@example.com");
        req.setPassword("Abcd@1234");
        req.setRoleName(RoleName.USER);

        mockMvc.perform(post("/api/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(content().string("User registered successfully"));

        assertThat(userCredentialRepository.existsByEmail("user1@example.com")).isTrue();
    }

    @Test
    void register_duplicateEmail_returnsMessage() throws Exception {
        createCredential("dup@example.com", "Abcd@1234", RoleName.USER, true);

        RegisterRequest req = new RegisterRequest();
        req.setEmail("dup@example.com");
        req.setPassword("Abcd@1234");
        req.setRoleName(RoleName.USER);

        mockMvc.perform(post("/api/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(content().string("Email already exists"));
    }

    @Test
    void login_success_returnsJwt() throws Exception {
        createCredential("login@example.com", "Abcd@1234", RoleName.USER, true);

        LoginRequest req = new LoginRequest();
        req.setEmail("login@example.com");
        req.setPassword("Abcd@1234");

        String token = mockMvc.perform(post("/api/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        assertThat(token.split("\\.")).hasSize(3);
    }

    @Test
    void login_wrongPassword_returns401() throws Exception {
        createCredential("wrong@example.com", "Abcd@1234", RoleName.USER, true);

        LoginRequest req = new LoginRequest();
        req.setEmail("wrong@example.com");
        req.setPassword("Wrong@1234");

        mockMvc.perform(post("/api/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error").value("Unauthorized"))
                .andExpect(jsonPath("$.message").value("Invalid email or password"));
    }
}
