package com.job_portal.job_service.dto.query;

import lombok.*;
import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JobSummaryQueryDto {
    private Long jobId;
    private String title;
    private String location;
    private Integer experienceRequired;
    private Instant postedDate;
}