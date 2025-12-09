package com.job_portal.job_service.controller.query;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/jobs")
@RequiredArgsConstructor
public class JobQueryController {
    private final JobQueryService jobQueryService;

    @GetMapping
    public ResponseEntity<Page<JobSummaryQueryDto>> listJobs(
            @RequestParam(required = false) String location,
            @RequestParam(required = false) Integer experience,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Page<JobSummaryQueryDto> result = jobQueryService.searchJobs(location, experience, page, size);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/{jobId}")
    public ResponseEntity<JobDetailsQueryDto> getJob(@PathVariable Long jobId) {
        JobDetailsQueryDto dto = jobQueryService.getJobDetails(jobId);
        return ResponseEntity.ok(dto);
    }
}
