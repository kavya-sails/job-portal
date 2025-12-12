package com.jobportal.user_service.service;

import com.jobportal.user_service.dto.LoginRequest;
import com.jobportal.user_service.dto.RegisterRequest;
import com.jobportal.user_service.entity.Role;
import com.jobportal.user_service.entity.UserCredential;
import com.jobportal.user_service.repository.RoleRepository;
import com.jobportal.user_service.repository.UserCredentialRepository;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserCredentialServiceTest {

    @Test
    void register_whenEmailExists_shouldReturnMessage() {
        UserCredentialRepository repo = mock(UserCredentialRepository.class);
        when(repo.existsByEmail("a@mail.com")).thenReturn(true);

        UserCredentialService service =
                new UserCredentialService(
                        repo,
                        mock(RoleRepository.class),
                        mock(PasswordEncoder.class),
                        mock(JwtService.class),
                        mock(AuthenticationManager.class)
                );

        RegisterRequest req = new RegisterRequest();
        req.setEmail("a@mail.com");

        assertEquals("Email already exists", service.register(req));
    }

    @Test
    void login_whenAuthenticated_shouldReturnToken() {
        Authentication auth = mock(Authentication.class);
        when(auth.isAuthenticated()).thenReturn(true);

        AuthenticationManager authManager = mock(AuthenticationManager.class);
        when(authManager.authenticate(any())).thenReturn(auth);

        UserCredential user = new UserCredential();
        user.setEmail("a@mail.com");
        user.setIsActive(true);

        UserCredentialRepository repo = mock(UserCredentialRepository.class);
        when(repo.findByEmail("a@mail.com")).thenReturn(Optional.of(user));

        JwtService jwtService = mock(JwtService.class);
        when(jwtService.generateToken(user)).thenReturn("TOKEN");

        UserCredentialService service =
                new UserCredentialService(
                        repo,
                        mock(RoleRepository.class),
                        mock(PasswordEncoder.class),
                        jwtService,
                        authManager
                );

        LoginRequest req = new LoginRequest();
        req.setEmail("a@mail.com");

        assertEquals("TOKEN", service.login(req));
    }

    @Test
    void findAll_shouldReturnUsers() {
        UserCredentialRepository repo = mock(UserCredentialRepository.class);
        when(repo.findAll()).thenReturn(List.of(new UserCredential()));

        UserCredentialService service =
                new UserCredentialService(
                        repo,
                        mock(RoleRepository.class),
                        mock(PasswordEncoder.class),
                        mock(JwtService.class),
                        mock(AuthenticationManager.class)
                );

        assertEquals(1, service.findAll().size());
    }
}
