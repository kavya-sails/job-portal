package com.job_portal.job_service.dto.command;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JobCommandDto {
    @NotBlank
    private String title;

    private String description;

    private String location;

    private Integer experienceRequired;

    /**
     * Optional: postedDate (if not provided, set to now)
     */
    private Instant postedDate;

    /**
     * Optional: expiresAt — admin can set expiry timestamp
     */
    private Instant expiresAt;
}