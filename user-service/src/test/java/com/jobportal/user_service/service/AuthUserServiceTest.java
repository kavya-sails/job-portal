package com.jobportal.user_service.service;

import com.jobportal.user_service.dto.LoginRequest;
import com.jobportal.user_service.dto.RegisterRequest;
import com.jobportal.user_service.entity.AuthUser;
import com.jobportal.user_service.entity.Role;
import com.jobportal.user_service.repository.AuthUserRepository;
import com.jobportal.user_service.repository.RoleRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthUserServiceTest {

    @Mock
    private AuthUserRepository authUserRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private AuthUserService authUserService;

    @Test
    void register_shouldSaveUser_whenEmailNotExists() {
        RegisterRequest request = new RegisterRequest();
        request.setEmail("test@example.com");
        request.setPassword("password");
        request.setRoleName("USER");

        Role role = Role.builder().roleId(1L).roleName("USER").build();

        when(authUserRepository.existsByEmail("test@example.com")).thenReturn(false);
        when(roleRepository.findByRoleName("USER")).thenReturn(Optional.of(role));
        when(passwordEncoder.encode("password")).thenReturn("encodedPassword");

        String result = authUserService.register(request);

        assertThat(result).isEqualTo("User registered successfully");

        ArgumentCaptor<AuthUser> captor = ArgumentCaptor.forClass(AuthUser.class);
        verify(authUserRepository).save(captor.capture());

        AuthUser saved = captor.getValue();
        assertThat(saved.getEmail()).isEqualTo("test@example.com");
        assertThat(saved.getPassword()).isEqualTo("encodedPassword");
        assertThat(saved.getRole().getRoleName()).isEqualTo("USER");
        assertThat(saved.getIsActive()).isTrue();
    }

    @Test
    void register_shouldReturnMessage_whenEmailAlreadyExists() {
        RegisterRequest request = new RegisterRequest();
        request.setEmail("existing@example.com");
        request.setPassword("pwd");
        request.setRoleName("USER");

        when(authUserRepository.existsByEmail("existing@example.com")).thenReturn(true);

        String result = authUserService.register(request);

        assertThat(result).isEqualTo("Username already exists");
        verifyNoMoreInteractions(roleRepository, passwordEncoder, jwtService);
    }

    @Test
    void login_shouldReturnToken_whenCredentialsValid() {
        LoginRequest request = new LoginRequest();
        request.setEmail("user@example.com");
        request.setPassword("rawPwd");

        Role role = Role.builder().roleName("USER").build();
        AuthUser user = AuthUser.builder()
                .userId(1L)
                .email("user@example.com")
                .password("encodedPwd")
                .role(role)
                .isActive(true)
                .build();

        when(authUserRepository.findByEmail("user@example.com"))
                .thenReturn(Optional.of(user));
        when(passwordEncoder.matches("rawPwd", "encodedPwd")).thenReturn(true);
        when(jwtService.generateToken(user)).thenReturn("jwt-token");

        String token = authUserService.login(request);

        assertThat(token).isEqualTo("jwt-token");
    }

    @Test
    void login_shouldReturnError_whenUserInactive() {
        LoginRequest request = new LoginRequest();
        request.setEmail("user@example.com");
        request.setPassword("rawPwd");

        AuthUser user = AuthUser.builder()
                .userId(1L)
                .email("user@example.com")
                .password("encodedPwd")
                .role(Role.builder().roleName("USER").build())
                .isActive(false)
                .build();

        when(authUserRepository.findByEmail("user@example.com"))
                .thenReturn(Optional.of(user));

        String result = authUserService.login(request);

        assertThat(result).isEqualTo("Account is deactivated");
        verify(passwordEncoder, never()).matches(any(), any());
        verify(jwtService, never()).generateToken(any());
    }
}
