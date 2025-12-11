package com.job_portal.job_service.service.command;

import com.job_portal.job_service.dto.command.JobCommandDto;
import com.job_portal.job_service.entity.JobEntity;
import com.job_portal.job_service.exception.JobNotFoundException;
import com.job_portal.job_service.repository.query.JobQueryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Service
@RequiredArgsConstructor
public class JobCommandService {
    private final JobQueryRepository jobRepo;

    public JobEntity createJob(JobCommandDto dto) {

        // determine postedDate (admin provided or now)
        Instant posted = dto.getPostedDate() == null ? Instant.now() : dto.getPostedDate();

        // compute expiresAt if expiryDays provided
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
                .packageOffered(dto.getPackageOffered())
                .skills(dto.getSkills())
                .education(dto.getEducation())
                .postedDate(posted)
                .expiresAt(expiresAt)
                .build();
        return jobRepo.save(j);
    }

    public JobEntity updateJob(Long jobId, JobCommandDto dto) {
        JobEntity job = jobRepo.findById(jobId).orElseThrow(() -> new JobNotFoundException(jobId));
        if (dto.getTitle() != null) job.setTitle(dto.getTitle());
        if (dto.getDescription() != null) job.setDescription(dto.getDescription());
        if (dto.getLocation() != null) job.setLocation(dto.getLocation());
        if (dto.getExperienceRequired() != null) job.setExperienceRequired(dto.getExperienceRequired());

        // new fields
        if (dto.getCompanyName() != null) job.setCompanyName(dto.getCompanyName());
        if (dto.getPackageOffered() != null) job.setPackageOffered(dto.getPackageOffered());
        if (dto.getSkills() != null) job.setSkills(dto.getSkills());
        if (dto.getEducation() != null) job.setEducation(dto.getEducation());

        // If admin provided a postedDate, update it
        if (dto.getPostedDate() != null) {
            job.setPostedDate(dto.getPostedDate());
        }

        // If expiryDays provided, recalc expiresAt relative to postedDate (newly provided or existing)
        if (dto.getExpiryDays() != null) {
            Instant basePosted = (dto.getPostedDate() != null) ? dto.getPostedDate() : job.getPostedDate();
            if (basePosted == null) basePosted = Instant.now();
            job.setExpiresAt(basePosted.plus(dto.getExpiryDays(), ChronoUnit.DAYS));
        }
        return jobRepo.save(job);
    }

    public void deleteJob(Long jobId) {
        JobEntity job = jobRepo.findById(jobId).orElseThrow(() -> new JobNotFoundException(jobId));
        jobRepo.delete(job);
    }
}