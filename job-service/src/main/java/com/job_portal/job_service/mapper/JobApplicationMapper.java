

package com.job_portal.job_service.mapper;

import com.job_portal.job_service.entity.ApplicationEntity;
import com.job_portal.job_service.entity.JobEntity;
import com.job_portal.job_service.dto.query.ApplicationHistoryQueryDto;
import com.job_portal.job_service.dto.query.JobDetailsQueryDto;

public class JobApplicationMapper {

    public static JobDetailsQueryDto toDetails(JobEntity e){
        if(e==null) return null;
        return JobDetailsQueryDto.builder()
                .jobId(e.getJobId())
                .title(e.getTitle())
                .description(e.getDescription())
                .location(e.getLocation())
                .experienceRequired(e.getExperienceRequired())
                .companyName(e.getCompanyName())
                .packageOffered(e.getPackageOffered())
                .skills(e.getSkills())
                .education(e.getEducation())
                .postedDate(e.getPostedDate())
                .expiresAt(e.getExpiresAt())
                .build();
    }
}