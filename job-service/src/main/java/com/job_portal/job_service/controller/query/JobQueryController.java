package com.job_portal.job_service.controller.query;

import com.job_portal.job_service.dto.query.JobDetailsQueryDto;
import com.job_portal.job_service.entity.JobEntity;
import com.job_portal.job_service.service.query.JobQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
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
}