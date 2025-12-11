package com.job_portal.job_service.controller.command;

import com.job_portal.job_service.dto.command.JobCommandDto;
import com.job_portal.job_service.entity.JobEntity;
import com.job_portal.job_service.service.command.JobCommandService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/jobs")
@RequiredArgsConstructor
public class JobCommandController {
    private final JobCommandService jobCommandService;

    // create a job
    @PostMapping
    public ResponseEntity<JobEntity> createJob(@Valid @RequestBody JobCommandDto dto) {
        JobEntity created = jobCommandService.createJob(dto);
        return ResponseEntity.ok(created);
    }

    // update a job (including extend expiry_date)
    @PutMapping("/{jobId}")
    public ResponseEntity<JobEntity> updateJob(@PathVariable Long jobId, @Valid @RequestBody JobCommandDto dto) {
        JobEntity updated = jobCommandService.updateJob(jobId, dto);
        return ResponseEntity.ok(updated);
    }

    // delete a job
    @DeleteMapping("/{jobId}")
    public ResponseEntity<Void> deleteJob(@PathVariable Long jobId) {
        jobCommandService.deleteJob(jobId);
        return ResponseEntity.noContent().build();
    }
}