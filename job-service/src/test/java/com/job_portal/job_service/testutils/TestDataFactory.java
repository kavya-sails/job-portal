package com.job_portal.job_service.testutils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.job_portal.job_service.dto.command.JobCommandDto;
import com.job_portal.job_service.dto.command.ApplicationStatusUpdateDto;
import com.job_portal.job_service.entity.ApplicationEntity;
import com.job_portal.job_service.entity.ApplicationStatus;
import com.job_portal.job_service.entity.JobEntity;

import java.time.Instant;

public class TestDataFactory {
    public static JobEntity sampleJobEntity() {
        return JobEntity.builder()
                .jobId(1L)
                .title("Backend Developer")
                .description("Build APIs")
                .location("Bengaluru")
                .experienceRequired(3)
                .companyName("Tech Corp")
                .packageOffered("8LPA")
                .skills("Java, Spring Boot")
                .education("B.Tech")
                .postedDate(Instant.now())
                .expiresAt(Instant.now().plusSeconds(3600 * 24 * 30))
                .build();
    }

    public static ApplicationEntity sampleApplicationEntity(JobEntity job) {
        return ApplicationEntity.builder()
                .applicationId(11L)
                .job(job)
                .userId("user-1")
                .companyName(job.getCompanyName())
                .appliedDate(Instant.now())
                .status(ApplicationStatus.PENDING)
                .build();
    }

    public static JobCommandDto sampleJobCommandDto() {
        return JobCommandDto.builder()
                .title("Backend Developer")
                .description("Responsible for server-side web application logic.")
                .location("Bengaluru")
                .experienceRequired(3)
                .companyName("Tech Corp")
                .expiryDays(10)
                .build();
    }

    public static ApplicationStatusUpdateDto sampleStatusUpdateDto() {
        return ApplicationStatusUpdateDto.builder()
                .status(ApplicationStatus.REVIEWED)
                .build();
    }

    public static String toJson(Object o) throws Exception {
        return new ObjectMapper().writeValueAsString(o);
    }
}