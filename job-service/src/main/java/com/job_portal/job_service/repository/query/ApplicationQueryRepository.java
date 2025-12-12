package com.job_portal.job_service.repository.query;

import com.job_portal.job_service.entity.ApplicationEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ApplicationQueryRepository extends JpaRepository<ApplicationEntity, Long>
{
    List<ApplicationEntity> findByUserIdOrderByAppliedDateDesc(Long userId);
}