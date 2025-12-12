package com.jobportal.user_service.repository;

import com.jobportal.user_service.entity.UserCredential;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface UserCredentialRepository extends JpaRepository<UserCredential, Long> {

    //  Used during LOGIN
    Optional<UserCredential> findByEmail(String email);

    //  Used during REGISTER to prevent duplicates
    boolean existsByEmail(String email);
}
