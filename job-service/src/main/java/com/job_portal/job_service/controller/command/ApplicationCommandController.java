package com.job_portal.job_service.controller.command;

import com.job_portal.job_service.dto.command.ApplicationStatusResponseDto;
import com.job_portal.job_service.dto.command.ApplicationStatusUpdateDto;
import com.job_portal.job_service.dto.query.ApplicationHistoryQueryDto;
import com.job_portal.job_service.exception.BadRequestException;
import com.job_portal.job_service.service.command.ApplicationCommandService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
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
            @RequestHeader("X-User-Id") String userId)
    {
        if (!StringUtils.hasText(userId)) {
            throw new BadRequestException("Missing or empty X-User-Id header");
//            return ResponseEntity.badRequest().build();
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
        ApplicationStatusResponseDto updated = commandService.updateStatus(applicationId, dto.getStatus());
        ApplicationStatusResponseDto resp = ApplicationStatusResponseDto.builder()
                .applicationId(updated.getApplicationId())
                .status(updated.getStatus())
                .build();
        return ResponseEntity.ok(resp);
    }
}