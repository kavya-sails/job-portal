package com.jobportal.user_service.dto;

import com.jobportal.user_service.enums.ApplicationStatus;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class JobStatusNotificationRequest {
    @NotNull
    private Long userId;
    @NotNull
    private Long jobId;
    @NotNull
    private ApplicationStatus status;
}