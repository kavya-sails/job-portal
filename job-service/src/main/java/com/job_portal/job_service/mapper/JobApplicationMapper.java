package com.job_portal.job_service.mapper;

import com.job_portal.job_service.dto.query.ApplicationHistoryQueryDto;
import com.job_portal.job_service.entity.ApplicationEntity;
import com.job_portal.job_service.entity.JobEntity;

public class JobApplicationMapper {
    public static JobSummaryQueryDto toSummary(JobEntity e) {
        if (e == null) return null;
        return JobSummaryQueryDto.builder()
                .jobId(e.getJobId())
                .title(e.getTitle())
                .location(e.getLocation())
                .experienceRequired(e.getExperienceRequired())
                .postedDate(e.getPostedDate())
                .build();
    }

    public static JobDetailsQueryDto toDetails(JobEntity e) {
        if (e == null) return null;
        return JobDetailsQueryDto.builder()
                .jobId(e.getJobId())
                .title(e.getTitle())
                .description(e.getDescription())
                .location(e.getLocation())
                .experienceRequired(e.getExperienceRequired())
                .postedDate(e.getPostedDate())
                .build();
    }

    public static ApplicationHistoryQueryDto toApplicationHistory(ApplicationEntity a) {
        if (a == null) return null;
        return ApplicationHistoryQueryDto.builder()
                .applicationId(a.getApplicationId())
                .jobId(a.getJob().getJobId())
                .jobTitle(a.getJob().getTitle())
                .appliedDate(a.getAppliedDate())
                .status(a.getStatus())
                .build();
    }
}
