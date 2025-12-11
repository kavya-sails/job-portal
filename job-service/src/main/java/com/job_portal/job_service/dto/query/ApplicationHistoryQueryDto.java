package com.job_portal.job_service.dto.query;

import com.job_portal.job_service.entity.ApplicationStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApplicationHistoryQueryDto {
    private Long applicationId;
    private Long jobId;
    private String jobTitle;
    private String companyName;
    private Instant appliedDate;
    private ApplicationStatus status;
}
