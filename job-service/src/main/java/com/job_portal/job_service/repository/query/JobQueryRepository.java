package com.job_portal.job_service.repository.query;


import com.job_portal.job_service.entity.JobEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.time.Instant;
import java.util.List;

public interface JobQueryRepository extends JpaRepository<JobEntity, Long>, JpaSpecificationExecutor<JobEntity> {
    List<JobEntity> findAll();

    // helper to remove expired
    List<JobEntity> findByExpiresAtBefore(Instant t);
}