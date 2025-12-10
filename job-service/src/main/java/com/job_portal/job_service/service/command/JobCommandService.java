package com.job_portal.job_service.service.command;

import com.job_portal.job_service.dto.command.JobCommandDto;
import com.job_portal.job_service.entity.JobEntity;
import com.job_portal.job_service.exception.JobNotFoundException;
import com.job_portal.job_service.repository.query.JobQueryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class JobCommandService {

//    private final JobQueryRepository jobRepo;
//
//    public List<JobDetailsQueryDto> getAllJobs() {
//        List<JobEntity> jobs = jobRepo.findAll();
//        return jobs.stream().map(JobApplicationMapper::toDetails).collect(Collectors.toList());
//    }
//
//    public JobDetailsQueryDto getJobDetails(Long jobId) {
//        JobEntity job = jobRepo.findById(jobId).orElseThrow(() -> new JobNotFoundException(jobId));
//        return JobApplicationMapper.toDetails(job);
//    }

    private final JobQueryRepository jobRepo;

    public JobEntity createJob(JobCommandDto dto) {
        JobEntity j = JobEntity.builder()
                .title(dto.getTitle())
                .description(dto.getDescription())
                .location(dto.getLocation())
                .experienceRequired(dto.getExperienceRequired())
                .postedDate(dto.getPostedDate() == null ? Instant.now() : dto.getPostedDate())
                .expiresAt(dto.getExpiresAt())
                .build();
        return jobRepo.save(j);
    }

    public JobEntity updateJob(Long jobId, JobCommandDto dto) {
        JobEntity job = jobRepo.findById(jobId).orElseThrow(() -> new JobNotFoundException(jobId));
        if (dto.getTitle() != null) job.setTitle(dto.getTitle());
        if (dto.getDescription() != null) job.setDescription(dto.getDescription());
        if (dto.getLocation() != null) job.setLocation(dto.getLocation());
        if (dto.getExperienceRequired() != null) job.setExperienceRequired(dto.getExperienceRequired());
        if (dto.getPostedDate() != null) job.setPostedDate(dto.getPostedDate());
        if (dto.getExpiresAt() != null) job.setExpiresAt(dto.getExpiresAt());
        return jobRepo.save(job);
    }

    public void deleteJob(Long jobId) {
        JobEntity job = jobRepo.findById(jobId).orElseThrow(() -> new JobNotFoundException(jobId));
        jobRepo.delete(job);
    }
}