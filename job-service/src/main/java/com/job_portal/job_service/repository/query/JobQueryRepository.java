package com.job_portal.job_service.repository.query;


import com.job_portal.job_service.entity.JobEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface JobQueryRepository extends JpaRepository<JobEntity, Long> {
    List<JobEntity> findAll();

}