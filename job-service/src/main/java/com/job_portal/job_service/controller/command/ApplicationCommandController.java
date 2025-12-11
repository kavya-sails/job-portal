package com.job_portal.job_service.controller.command;

import com.job_portal.job_service.dto.command.ApplicationStatusResponseDto;
import com.job_portal.job_service.dto.command.ApplicationStatusUpdateDto;
import com.job_portal.job_service.entity.ApplicationEntity;
import com.job_portal.job_service.service.command.ApplicationCommandService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/jobs/applications")
@RequiredArgsConstructor
public class ApplicationCommandController
{
    private final ApplicationCommandService commandService;
    @PostMapping("/{jobId}")
    public ResponseEntity<ApplicationEntity> apply(
            @PathVariable Long jobId,
            @RequestHeader("X-User-Id") String userId)
    {
        if (userId == null) return ResponseEntity.badRequest().body(null);
        ApplicationEntity saved = commandService.apply(userId, jobId);
        return ResponseEntity.ok(saved);
    }

    // Update application status
    @PutMapping("/{applicationId}/status")
    public ResponseEntity<?> updateStatus(
            @PathVariable Long applicationId,
            @RequestBody ApplicationStatusUpdateDto dto)
    {
        ApplicationEntity updated = commandService.updateStatus(applicationId, dto.getStatus());
        ApplicationStatusResponseDto resp = ApplicationStatusResponseDto.builder()
                .applicationId(updated.getApplicationId())
                .status(updated.getStatus())
                .build();
        return ResponseEntity.ok(resp);
    }
}