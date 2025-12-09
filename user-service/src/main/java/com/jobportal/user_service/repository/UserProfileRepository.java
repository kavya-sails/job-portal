package com.jobportal.user_service.repository;

import com.jobportal.user_service.entity.UserProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserProfileRepository extends JpaRepository<UserProfile, Long> {

    //  Find user by email (used for profile fetch, validation)
    Optional<UserProfile> findByEmail(String email);

    //  Fetch only active users (for soft delete logic)
    List<UserProfile> findByIsActiveTrue();
}
