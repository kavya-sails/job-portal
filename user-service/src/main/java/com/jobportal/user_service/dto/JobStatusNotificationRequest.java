package com.jobportal.user_service.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class JobStatusNotificationRequest {

    @NotNull
    private Long userId;   // This matches UserData.id (for now)

    @NotNull
    private Long jobId;

    @NotBlank
    private String status;  // ACCEPTED or REJECTED
}
