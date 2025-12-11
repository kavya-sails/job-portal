package com.jobportal.user_service.repository;

import com.jobportal.user_service.entity.UserProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserProfileRepository extends JpaRepository<UserProfile, Long> {
    // no extra methods required for now
}
