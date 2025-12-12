package com.job_portal.job_service.dto.command;

import com.job_portal.job_service.entity.ApplicationStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Response showing updated application status")
public class ApplicationStatusResponseDto {
    @Schema(description = "Application ID", example = "101")
    private Long applicationId;
    @Schema(description = "Updated application status", example = "REVIEWED")
    private ApplicationStatus status;
}