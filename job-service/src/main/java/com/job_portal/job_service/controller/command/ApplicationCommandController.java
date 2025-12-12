package com.job_portal.job_service.controller.command;

import com.job_portal.job_service.dto.command.ApplicationStatusResponseDto;
import com.job_portal.job_service.dto.command.ApplicationStatusUpdateDto;
import com.job_portal.job_service.dto.query.ApplicationHistoryQueryDto;
import com.job_portal.job_service.entity.ApplicationStatus;
import com.job_portal.job_service.exception.InvalidApplicationStatusException;
import com.job_portal.job_service.exception.MissingUserIdHeaderException;
import com.job_portal.job_service.service.command.ApplicationCommandService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/jobs/applications")
@RequiredArgsConstructor
public class ApplicationCommandController
{
    private final ApplicationCommandService commandService;
    @PostMapping("/{jobId}")
    public ResponseEntity<ApplicationHistoryQueryDto> apply(
            @PathVariable Long jobId,
            @RequestHeader(value = "X-User-Id", required = false) Long userId)
    {
        if (userId == null) {
            throw new MissingUserIdHeaderException();
        }
        ApplicationHistoryQueryDto dto = commandService.apply(userId, jobId);
        return ResponseEntity.ok(dto);
    }

    // Update application status
    @PutMapping("/{applicationId}/status")
    public ResponseEntity<ApplicationStatusResponseDto> updateStatus(
            @PathVariable Long applicationId,
            @RequestBody ApplicationStatusUpdateDto dto)
    {
        if (dto == null || ObjectUtils.isEmpty(dto.getStatus())) {
            throw new InvalidApplicationStatusException("Status is required and cannot be null or empty");
        }
        try {
            ApplicationStatus.valueOf(dto.getStatus().name());
        } catch (IllegalArgumentException ex) {
            throw new InvalidApplicationStatusException("Invalid application status: " + dto.getStatus());
        }
        ApplicationStatusResponseDto updated = commandService.updateStatus(applicationId, dto.getStatus());
        ApplicationStatusResponseDto resp = ApplicationStatusResponseDto.builder()
                .applicationId(updated.getApplicationId())
                .status(updated.getStatus())
                .build();
        return ResponseEntity.ok(resp);
    }
}