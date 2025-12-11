package com.job_portal.job_service.dto.query;

import lombok.*;
import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JobDetailsQueryDto {
    private Long jobId;
    private String title;
    private String description;
    private String location;
    private Integer experienceRequired;

    private String companyName;
    private String packageOffered;
    private String skills;
    private String education;

    private Instant postedDate;
    private Instant expiresAt;
}