package com.jobportal.user_service.service;

import com.jobportal.user_service.dto.LoginRequest;
import com.jobportal.user_service.dto.RegisterRequest;
import com.jobportal.user_service.entity.UserCredential;
import com.jobportal.user_service.entity.Role;
import com.jobportal.user_service.exception.UserAlreadyExistsException;
import com.jobportal.user_service.repository.UserCredentialRepository;
import com.jobportal.user_service.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserCredentialService {
    private final UserCredentialRepository userCredentialRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public String register(RegisterRequest request) throws UserAlreadyExistsException {
        if (userCredentialRepository.existsByEmail(request.getEmail())) {
            throw new UserAlreadyExistsException("User with email " + request.getEmail() + " already exists");
        }
        Role role = roleRepository.findByRoleName(request.getRoleName())
                .orElseThrow(() -> new RuntimeException("Role not found"));
        UserCredential user = new UserCredential();
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword())); //  encrypted
        user.setRole(role);
        user.setIsActive(true);
        userCredentialRepository.save(user);
        return "User registered successfully";
    }

    public String login(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        //  If authenticated, fetch user & generate token
        if (authentication.isAuthenticated()) {
            UserCredential user = userCredentialRepository.findByEmail(request.getEmail())
                    .orElseThrow(() -> new RuntimeException("User not found"));
            if (!user.getIsActive()) {
                return "Account is deactivated";
            }
            return jwtService.generateToken(user);
        }
        throw new RuntimeException("Invalid email or password");
    }

    public List<UserCredential> findAll() {
        return userCredentialRepository.findAll();
    }
}
