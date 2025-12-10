package com.job_portal.job_service.controller.command;

import com.job_portal.job_service.dto.command.ApplicationStatusUpdateDto;
import com.job_portal.job_service.entity.ApplicationEntity;
import com.job_portal.job_service.service.command.ApplicationCommandService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/applications")
@RequiredArgsConstructor
public class ApplicationCommandController
{
    private final ApplicationCommandService commandService;

    /**
     * Apply to a job.
     * jobId comes from the URL.
     * userId comes from API Gateway header.
     */
    @PostMapping("/{jobId}")
    public ResponseEntity<ApplicationEntity> apply(
            @PathVariable Long jobId,
            HttpServletRequest request
    ) {
        String userId = request.getHeader("X-USER-ID");
        String role = request.getHeader("X-ROLE");

        if (userId == null) {
            return ResponseEntity.badRequest().body(null);
        }

        // Only USER can apply, not ADMIN
        if (role == null || !role.equalsIgnoreCase("USER")) {
            return ResponseEntity.status(403).build();
        }

        ApplicationEntity saved = commandService.apply(userId, jobId);
        return ResponseEntity.ok(saved);
    }
    // ADMIN: UPDATE STATUS
// =======================
    @PutMapping("/{applicationId}/status")
    public ResponseEntity<?> updateStatus(
            @PathVariable Long applicationId,
            @RequestBody ApplicationStatusUpdateDto dto,
            HttpServletRequest request
    ) {
        String role = request.getHeader("X-ROLE");

        // Only ADMIN is allowed
        if (role == null || !role.equalsIgnoreCase("ADMIN")) {
            return ResponseEntity.status(403).body("Only admin can update application status");
        }

        return ResponseEntity.ok(commandService.updateStatus(applicationId, dto.getStatus()));
    }
}
