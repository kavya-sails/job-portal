package com.job_portal.job_service.dto.query;

import lombok.Data;

@Data
public class JobSearchCriteria {
    private String title;
    private String location;
    private Integer experienceRequired;
}
