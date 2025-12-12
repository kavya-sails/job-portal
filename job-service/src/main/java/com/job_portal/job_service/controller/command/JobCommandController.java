package com.job_portal.job_service.controller.command;

import com.job_portal.job_service.dto.command.JobCommandDto;
import com.job_portal.job_service.dto.query.JobDetailsQueryDto;
import com.job_portal.job_service.service.command.JobCommandService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/jobs")
@RequiredArgsConstructor
@Tag(name = "Job Commands", description = "APIs for creating, updating, deleting jobs")
public class JobCommandController {
    private final JobCommandService jobCommandService;

    // create a job
    @PostMapping
    @Operation(
            summary = "Create a new job",
            description = "Creates a new job post with the provided job details"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Job created successfully"),
            @ApiResponse(responseCode = "400", description = "Validation failed"),
    })
    public ResponseEntity<JobDetailsQueryDto> createJob(@Valid @RequestBody JobCommandDto dto) {
        JobDetailsQueryDto created = jobCommandService.createJob(dto);
        return ResponseEntity.ok(created);
    }

    // update a job (including extend expiry_date)
    @PutMapping("/{jobId}")
    @Operation(
            summary = "Update a job",
            description = "Updates an existing job with new information"
    )
    public ResponseEntity<JobDetailsQueryDto> updateJob(@PathVariable Long jobId, @RequestBody JobCommandDto dto) {
        JobDetailsQueryDto updated = jobCommandService.updateJob(jobId, dto);
        return ResponseEntity.ok(updated);
    }

    // delete a job
    @DeleteMapping("/{jobId}")
    @Operation(
            summary = "Delete a job",
            description = "Deletes a job by its ID"
    )
    public ResponseEntity<Void> deleteJob(@PathVariable Long jobId) {
        jobCommandService.deleteJob(jobId);
        return ResponseEntity.noContent().build();
    }
}