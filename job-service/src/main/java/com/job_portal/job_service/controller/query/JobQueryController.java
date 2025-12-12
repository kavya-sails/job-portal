package com.job_portal.job_service.controller.query;

import com.job_portal.job_service.dto.query.JobDetailsQueryDto;
import com.job_portal.job_service.dto.query.JobSearchCriteria;
import com.job_portal.job_service.service.query.JobQueryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/jobs")
@RequiredArgsConstructor
@Tag(name = "Job Queries", description = "Search and retrieve job details")
public class JobQueryController {

    private final JobQueryService jobQueryService;

    @GetMapping("/{jobId}")
    @Operation(summary = "Get job details", description = "Retrieve full job information by ID")
    public ResponseEntity<JobDetailsQueryDto> getJob(@PathVariable Long jobId) {
        JobDetailsQueryDto dto = jobQueryService.getJobDetails(jobId);
        return ResponseEntity.ok(dto);
    }

    // Search jobs with filters: title, location, experience
    @GetMapping
    @Operation(summary = "Search jobs", description = "Search jobs using title, location, company, experience")
    public ResponseEntity<Page<JobDetailsQueryDto>> searchJobs(
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String location,
            @RequestParam(required = false) Integer experienceRequired,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String companyName
            ) {
        JobSearchCriteria criteria = new JobSearchCriteria();
        criteria.setTitle(title);
        criteria.setLocation(location);
        criteria.setExperienceRequired(experienceRequired);
        criteria.setCompanyName(companyName);
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(jobQueryService.searchJobs(criteria, pageable));
    }
}