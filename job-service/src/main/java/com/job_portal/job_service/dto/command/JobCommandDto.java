package com.job_portal.job_service.dto.command;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Request body for creating or updating a job post")
public class JobCommandDto {

    @Schema(description = "Job title", example = "Senior Java Developer", required = true)
    @NotBlank(message = "Job title is required and cannot be empty")
    @Size(min = 3, max = 100, message = "Job title must be between 3 and 100 characters")
    private String title;

    @NotBlank(message = "Job description is required and cannot be empty")
    @Size(min = 20, max = 5000, message = "Description must be between 20 and 5000 characters")
    @Schema(description = "Job location", example = "Hyderabad", required = true)
    private String description;

    @NotBlank(message = "Location is required and cannot be empty")
    @Size(min = 2, max = 255, message = "Location must be between 2 and 255 characters")
    @Schema(description = "Job location", example = "Hyderabad", required = true)
    private String location;

    @NotNull(message = "Experience required is mandatory")
    @Min(value = 0, message = "Experience cannot be negative")
    @Max(value = 30, message = "Experience cannot exceed 50 years")
    @Schema(description = "Required experience in years", example = "3", required = true)
    private Integer experienceRequired;

    @NotBlank(message = "Company name is required")
    @Size(min = 2, max = 255, message = "Company name must be between 2 and 255 characters")
    @Schema(description = "Company offering the job", example = "Google", required = true)
    private String companyName;

    @NotBlank(message = "packageOffered is required and cannot be empty")
    @Size(max = 255, message = "Package offered must be at most 255 characters")
    @Schema(description = "Package offered", example = "12 LPA")
    @Pattern(regexp = "^\\d+(?:\\.\\d+)?-\\d+(?:\\.\\d+)? LPA$", message = "Salary range must be in format: '13-14 LPA' or '13.2-14.5 LPA'")
    private String packageOffered;

    @NotBlank(message = "Skills field is required (recruiter must provide relevant skills)")
    @Size(min = 5, max = 2000, message = "Skills must be between 5 and 2000 characters")
    @Schema(description = "Required job skills", example = "Java, Spring Boot, Microservices", required = true)
    private String skills;

    @NotBlank(message = "Education details are required")
    @Size(min = 3, max = 2000, message = "Education must be between 3 and 2000 characters")
    @Schema(description = "Education qualifications required", example = "B.Tech / M.Tech", required = true)
    private String education;

    @PastOrPresent(message = "Posted date cannot be in the future")
    @Schema(description = "Job posted date", example = "2025-01-15T05:30:00Z")
    private Instant postedDate;

    @NotNull(message = "Expiry days field is required")
    @Min(value = 1, message = "Expiry days must be at least 1")
    @Max(value = 90, message = "Expiry days cannot exceed 10 years (3650 days)")
    @Schema(description = "Number of days before job expires", example = "30", required = true)
    private Integer expiryDays;
}