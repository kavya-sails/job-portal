package com.job_portal.job_service.dto.command;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApplyJobCommand {

    @NotNull
    private Long jobId;

    @NotBlank
    private String userId;
}