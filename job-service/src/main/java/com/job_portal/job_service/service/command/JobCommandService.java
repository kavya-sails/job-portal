package com.job_portal.job_service.service.command;

import com.job_portal.job_service.dto.command.JobCommandDto;
import com.job_portal.job_service.dto.query.JobDetailsQueryDto;
import com.job_portal.job_service.entity.JobEntity;
import com.job_portal.job_service.exception.JobNotFoundException;
import com.job_portal.job_service.mapper.JobApplicationMapper;
import com.job_portal.job_service.repository.query.JobQueryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Service
@RequiredArgsConstructor
public class JobCommandService {
    private final JobQueryRepository jobRepo;

    @Transactional
    public JobDetailsQueryDto createJob(JobCommandDto dto) {

        Instant posted = dto.getPostedDate() == null ? Instant.now() : dto.getPostedDate();

        Instant expiresAt = null;
        if (dto.getExpiryDays() != null && dto.getExpiryDays() > 0) {
            expiresAt = posted.plus(dto.getExpiryDays(), ChronoUnit.DAYS);
        }

        JobEntity j = JobEntity.builder()
                .title(dto.getTitle())
                .description(dto.getDescription())
                .location(dto.getLocation())
                .experienceRequired(dto.getExperienceRequired())
                .companyName(dto.getCompanyName())
                .packageOffered(dto.getPackageOffered() == null ? "" : dto.getPackageOffered())
                .skills(dto.getSkills() == null ? "" : dto.getSkills())
                .education(dto.getEducation() == null ? "" : dto.getEducation())
                .postedDate(posted)
                .expiresAt(expiresAt)
                .build();
        JobEntity saved = jobRepo.save(j);
        return JobApplicationMapper.toDetails(saved);
    }


    @Transactional
    public JobDetailsQueryDto updateJob(Long jobId, JobCommandDto dto) {
        JobEntity job = jobRepo.findById(jobId).orElseThrow(() -> new JobNotFoundException(jobId));
        if (dto.getTitle() != null) job.setTitle(dto.getTitle());
        if (dto.getDescription() != null) job.setDescription(dto.getDescription());
        if (dto.getLocation() != null) job.setLocation(dto.getLocation());
        if (dto.getExperienceRequired() != null) job.setExperienceRequired(dto.getExperienceRequired());
        if (dto.getCompanyName() != null) job.setCompanyName(dto.getCompanyName());
        if (dto.getPackageOffered() != null) job.setPackageOffered(dto.getPackageOffered());
        if (dto.getSkills() != null) job.setSkills(dto.getSkills());
        if (dto.getEducation() != null) job.setEducation(dto.getEducation());

        if (dto.getPostedDate() != null) {
            job.setPostedDate(dto.getPostedDate());
        }

        if (dto.getExpiryDays() != null) {
            Instant basePosted = (dto.getPostedDate() != null) ? dto.getPostedDate() : job.getPostedDate();
            if (basePosted == null) basePosted = Instant.now();
            job.setExpiresAt(basePosted.plus(dto.getExpiryDays(), ChronoUnit.DAYS));
        }
        JobEntity updated = jobRepo.save(job);
        return JobApplicationMapper.toDetails(updated);
    }


    @Transactional
    public void deleteJob(Long jobId) {
        JobEntity job = jobRepo.findById(jobId).orElseThrow(() -> new JobNotFoundException(jobId));
        jobRepo.delete(job);
    }
}