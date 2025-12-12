package com.jobportal.user_service.integration;

import com.jobportal.user_service.entity.Role;
import com.jobportal.user_service.entity.UserCredential;
import com.jobportal.user_service.enums.RoleName;
import com.jobportal.user_service.repository.RoleRepository;
import com.jobportal.user_service.repository.UserCredentialRepository;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;

public abstract class IntegrationTestBase {

    @Autowired protected RoleRepository roleRepository;
    @Autowired protected UserCredentialRepository userCredentialRepository;
    @Autowired protected PasswordEncoder passwordEncoder;

    @BeforeEach
    void seedRoles() {
        for (RoleName rn : RoleName.values()) {
            roleRepository.findByRoleName(rn).orElseGet(() ->
                    roleRepository.save(Role.builder().roleName(rn).build())
            );
        }
    }

    protected UserCredential createCredential(String email, String rawPassword, RoleName roleName, boolean active) {
        Role role = roleRepository.findByRoleName(roleName).orElseThrow();

        UserCredential uc = UserCredential.builder()
                .email(email)
                .password(passwordEncoder.encode(rawPassword))
                .role(role)
                .isActive(active)
                .build();

        return userCredentialRepository.save(uc); // generates userId
    }
}
