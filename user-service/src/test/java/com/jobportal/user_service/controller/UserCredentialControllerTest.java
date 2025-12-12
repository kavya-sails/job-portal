package com.jobportal.user_service.controller;

import com.jobportal.user_service.dto.LoginRequest;
import com.jobportal.user_service.dto.RegisterRequest;
import com.jobportal.user_service.entity.UserCredential;
import com.jobportal.user_service.enums.RoleName;
import com.jobportal.user_service.service.UserCredentialService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserCredentialControllerTest {

    @Mock
    private UserCredentialService userCredentialService;

    @InjectMocks
    private UserCredentialController userCredentialController;

    private RegisterRequest registerRequest;
    private LoginRequest loginRequest;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        registerRequest = new RegisterRequest();
        registerRequest.setEmail("test@example.com");
        registerRequest.setPassword("Password@123");
        registerRequest.setRoleName(RoleName.USER);

        loginRequest = new LoginRequest();
        loginRequest.setEmail("test@example.com");
        loginRequest.setPassword("Password@123");
    }

    // REGISTER USER
    @Test
    void testRegisterUser() {
        when(userCredentialService.register(registerRequest))
                .thenReturn("User registered successfully");

        ResponseEntity<String> response =
                userCredentialController.register(registerRequest);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals("User registered successfully", response.getBody());
        verify(userCredentialService, times(1)).register(registerRequest);
    }

    // LOGIN USER
    @Test
    void testLoginUser() {
        when(userCredentialService.login(loginRequest))
                .thenReturn("Login successful");

        ResponseEntity<String> response =
                userCredentialController.login(loginRequest);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals("Login successful", response.getBody());
        verify(userCredentialService, times(1)).login(loginRequest);
    }

    // GET ALL USERS
    @Test
    void testGetAllUsers() {
        UserCredential user = new UserCredential();
        user.setUserId(1L);
        user.setEmail("test@example.com");

        when(userCredentialService.findAll())
                .thenReturn(List.of(user));

        List<UserCredential> result = userCredentialController.getAllUsers();

        assertEquals(1, result.size());
        assertEquals("test@example.com", result.get(0).getEmail());
        verify(userCredentialService, times(1)).findAll();
    }
}
