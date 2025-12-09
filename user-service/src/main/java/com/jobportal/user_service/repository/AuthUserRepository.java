package com.jobportal.user_service.repository;

import com.jobportal.user_service.entity.AuthUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AuthUserRepository extends JpaRepository<AuthUser, Long> {

    //  Used during LOGIN
    Optional<AuthUser> findByUsername(String username);

    //  Used during REGISTER to prevent duplicates
    boolean existsByUsername(String username);
}
