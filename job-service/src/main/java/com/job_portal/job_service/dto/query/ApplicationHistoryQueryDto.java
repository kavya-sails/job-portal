package com.job_portal.job_service.dto.query;

import com.job_portal.job_service.entity.ApplicationStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Application history details for a user")
public class ApplicationHistoryQueryDto {
    @Schema(description = "Application ID", example = "501")
    private Long applicationId;

    @Schema(description = "Job ID related to application", example = "12")
    private Long jobId;

    @Schema(description = "Job title", example = "Java Developer")
    private String jobTitle;

    @Schema(description = "Company name", example = "Microsoft")
    private String companyName;

    @Schema(description = "Date of application", example = "2025-01-20T10:15:00Z")
    private Instant appliedDate;

    @Schema(description = "Current status", example = "PENDING")
    private ApplicationStatus status;
}
