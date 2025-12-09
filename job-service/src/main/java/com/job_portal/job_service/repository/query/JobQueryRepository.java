package com.job_portal.job_service.repository.query;


import com.job_portal.job_service.entity.JobEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JobQueryRepository extends JpaRepository<JobEntity, Long> {
    Page<JobEntity> findByLocationContainingIgnoreCaseAndExperienceRequiredLessThanEqual(String location, Integer experience, Pageable pageable);

    Page<JobEntity> findByLocationContainingIgnoreCase(String location, Pageable pageable);
}