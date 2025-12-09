package com.job_portal.job_service.service.query;

import com.job_portal.job_service.entity.JobEntity;
import com.job_portal.job_service.exception.JobNotFoundException;
import com.job_portal.job_service.dto.query.JobDetailsQueryDto;
import com.job_portal.job_service.repository.query.JobQueryRepository;
import com.job_portal.job_service.mapper.JobApplicationMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class JobQueryService {

    private final JobQueryRepository jobRepo;

    public List<JobEntity> getAllJobs() {
        return jobRepo.findAll();
    }

    public JobDetailsQueryDto getJobDetails(Long jobId) {
        JobEntity job = jobRepo.findById(jobId).orElseThrow(() -> new JobNotFoundException(jobId));
        return JobApplicationMapper.toDetails(job);
    }
}