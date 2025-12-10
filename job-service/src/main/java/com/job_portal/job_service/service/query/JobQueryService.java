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
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class JobQueryService {

    private final JobQueryRepository jobRepo;

    public List<JobDetailsQueryDto> getAllJobs() {
        List<JobEntity> jobs = jobRepo.findAll();
        return jobs.stream().map(JobApplicationMapper::toDetails).collect(Collectors.toList());
    }

    public JobDetailsQueryDto getJobDetails(Long jobId) {
        JobEntity job = jobRepo.findById(jobId).orElseThrow(() -> new JobNotFoundException(jobId));
        return JobApplicationMapper.toDetails(job);
    }
}