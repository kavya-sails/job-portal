package com.job_portal.job_service.dto.command;

import com.job_portal.job_service.entity.ApplicationStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApplicationStatusResponseDto {
    private Long applicationId;
    private ApplicationStatus status;
}
