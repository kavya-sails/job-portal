package com.jobportal.user_service.service;

import com.jobportal.user_service.dto.LoginRequest;
import com.jobportal.user_service.dto.RegisterRequest;
import com.jobportal.user_service.entity.AuthUser;
import com.jobportal.user_service.entity.Role;
import com.jobportal.user_service.repository.UserRepository;
import com.jobportal.user_service.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public String register(RegisterRequest request) {

        //  Check if username already exists
        if (userRepository.existsByEmail(request.getEmail())) {
            return "Username already exists";
        }

        //  Find role (USER / ADMIN)
        Role role = roleRepository.findByRoleName("USER")
                .orElseThrow(() -> new RuntimeException("Role not found"));

        //  Create AuthUser
        AuthUser user = new AuthUser();
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword())); // encrypted
        user.setRole(role);
        user.setIsActive(true);

        userRepository.save(user);

        return "User registered successfully";
    }

    public String login(LoginRequest request) {

        AuthUser user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Invalid username"));

        if (!user.getIsActive()) {
            return "Account is deactivated";
        }

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            return "Invalid password";
        }

        //  Generate JWT Token
        return jwtService.generateToken(user);
    }

    public List<AuthUser> findAll() {
        return userRepository.findAll();
    }
}
