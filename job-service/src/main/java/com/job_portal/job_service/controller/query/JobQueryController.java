package com.job_portal.job_service.controller.query;

import com.job_portal.job_service.dto.query.JobDetailsQueryDto;
import com.job_portal.job_service.dto.query.JobSearchCriteria;
import com.job_portal.job_service.service.query.JobQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/jobs")
@RequiredArgsConstructor
public class JobQueryController {

    private final JobQueryService jobQueryService;
    @GetMapping
    public ResponseEntity<List<JobDetailsQueryDto>> listJobs() {
        List<JobDetailsQueryDto> result = jobQueryService.getAllJobs();
        return ResponseEntity.ok(result);
    }

    @GetMapping("/{jobId}")
    public ResponseEntity<JobDetailsQueryDto> getJob(@PathVariable Long jobId) {
        JobDetailsQueryDto dto = jobQueryService.getJobDetails(jobId);
        return ResponseEntity.ok(dto);
    }

    // Search jobs with filters: title, location, experience
    @GetMapping("/search")
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