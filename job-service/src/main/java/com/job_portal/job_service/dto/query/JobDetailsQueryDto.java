package com.job_portal.job_service.dto.query;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Full job details returned in responses")
public class JobDetailsQueryDto {
    @Schema(description = "Job ID", example = "12")
    private Long jobId;

    @Schema(description = "Job title", example = "Java Developer")
    private String title;

    @Schema(description = "Job description", example = "We are hiring...")
    private String description;

    @Schema(description = "Job location", example = "Hyderabad")
    private String location;

    @Schema(description = "Required experience", example = "2")
    private Integer experienceRequired;

    @Schema(description = "Company name", example = "Amazon")
    private String companyName;

    @Schema(description = "Package offered", example = "15 LPA")
    private String packageOffered;

    @Schema(description = "Skills required", example = "Java, Spring Boot")
    private String skills;

    @Schema(description = "Education required", example = "B.Tech / M.Tech")
    private String education;

    @Schema(description = "Posted date", example = "2025-01-10T08:30:00Z")
    private Instant postedDate;

    @Schema(description = "When the job expires", example = "2025-02-10T00:00:00Z")
    private Instant expiresAt;
}