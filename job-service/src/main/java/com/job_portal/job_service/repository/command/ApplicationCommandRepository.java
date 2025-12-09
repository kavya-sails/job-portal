package com.job_portal.job_service.repository.command;

import com.job_portal.job_service.entity.ApplicationEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ApplicationCommandRepository extends JpaRepository<ApplicationEntity, Long>
{
    Optional<ApplicationEntity> findByJobJobIdAndUserId(Long jobId, String userId);
}
