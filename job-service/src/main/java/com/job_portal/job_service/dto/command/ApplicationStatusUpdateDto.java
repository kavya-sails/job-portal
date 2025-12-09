package com.job_portal.job_service.dto.command;

import com.job_portal.job_service.entity.ApplicationStatus;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApplicationStatusUpdateDto {
    @NotNull
    private ApplicationStatus status;
}