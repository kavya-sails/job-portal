package com.jobportal.user_service.service;

import com.jobportal.user_service.dto.RegisterRequest;
import com.jobportal.user_service.entity.AuthUser;
import com.jobportal.user_service.entity.Role;
import com.jobportal.user_service.repository.AuthUserRepository;
import com.jobportal.user_service.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthUserService {

    @Autowired
    private AuthUserRepository authUserRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public String register(RegisterRequest request) {

        //  Check if username already exists
        if (authUserRepository.existsByUsername(request.getUsername())) {
            return "Username already exists";
        }

        //  Find role (USER / ADMIN)
        Role role = roleRepository.findByRoleName(request.getRoleName())
                .orElseThrow(() -> new RuntimeException("Role not found"));

        //  Create AuthUser
        AuthUser user = new AuthUser();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword())); // encrypted
        user.setRole(role);
        user.setIsActive(true);

        authUserRepository.save(user);

        return "User registered successfully";
    }
}
