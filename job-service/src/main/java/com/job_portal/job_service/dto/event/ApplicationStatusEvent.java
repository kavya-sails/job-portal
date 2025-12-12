package com.job_portal.job_service.dto.event;

import com.job_portal.job_service.entity.ApplicationStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApplicationStatusEvent implements Serializable {
    private Long applicationId;
    private Long userId;
    private Long jobId;
    private String jobTitle;
    private ApplicationStatus status;
}