package com.jobportal.user_service.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class JobStatusNotificationRequest {

    @NotNull
    private Long userId;   // This matches UserData.id (for now)

    @NotNull
    private Long jobId;

    @NotBlank
    private String status;  // ACCEPTED or REJECTED
}
