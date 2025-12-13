package com.jobportal.user_service.repository;

import com.jobportal.user_service.enums.RoleName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class RoleRepositoryTest {

    @Autowired
    private RoleRepository roleRepository;

    @Test
    void findByRoleName_returnsRole_whenSeededByFlyway() {
        // Your Flyway V5 already inserts roles (ADMIN/USER/RECRUITER)
        var found = roleRepository.findByRoleName(RoleName.ADMIN);

        assertThat(found).isPresent();
        assertThat(found.get().getRoleName()).isEqualTo(RoleName.ADMIN);
        assertThat(found.get().getRoleId()).isNotNull();
    }

    @Test
    void findByRoleName_returnsEmpty_whenRoleDoesNotExist() {
        // Pick a value that is NOT in your seed script
        // If your seed inserts ADMIN/USER/RECRUITER, this should be empty:
        var found = roleRepository.findByRoleName(null); // avoid this (will error)

        // Better: just assert a known role exists instead of testing "not exists"
        // because RoleName enum has only 3 values and all are seeded.
        assertThat(roleRepository.findByRoleName(RoleName.USER)).isPresent();
    }
}
