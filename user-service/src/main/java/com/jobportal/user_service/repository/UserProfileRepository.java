package com.jobportal.user_service.repository;

import com.jobportal.user_service.entity.UserProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserProfileRepository extends JpaRepository<UserProfile, Long> {

    //  Fetch only active users (for soft delete logic)
    List<UserProfile> findByIsActiveTrue();
}
