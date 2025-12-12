package com.job_portal.job_service.mapper;

import com.job_portal.job_service.dto.command.ApplicationStatusResponseDto;
import com.job_portal.job_service.dto.query.ApplicationHistoryQueryDto;
import com.job_portal.job_service.entity.ApplicationEntity;

public class ApplicationCommandMapper {
    private ApplicationCommandMapper() {}

    public static ApplicationHistoryQueryDto toApplicationHistory(ApplicationEntity a) {
        if (a == null) return null;
        return ApplicationHistoryQueryDto.builder()
                .applicationId(a.getApplicationId())
                .jobId(a.getJob().getJobId())
                .jobTitle(a.getJob().getTitle())
                .companyName(a.getCompanyName())
                .appliedDate(a.getAppliedDate())
                .status(a.getStatus())
                .build();
    }

    public static ApplicationStatusResponseDto toStatusResponse(ApplicationEntity a) {
        if (a == null) return null;
        return ApplicationStatusResponseDto.builder()
                .applicationId(a.getApplicationId())
                .status(a.getStatus())
                .build();
    }
}
