package com.job_portal.job_service.controller.query;

import com.job_portal.job_service.dto.query.ApplicationHistoryQueryDto;
import com.job_portal.job_service.exception.ForbiddenException;
import com.job_portal.job_service.exception.MissingUserIdHeaderException;
import com.job_portal.job_service.service.query.ApplicationQueryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/jobs/applications")
@RequiredArgsConstructor
@Tag(name = "Application History", description = "User application history APIs")
public class ApplicationQueryController {
    private final ApplicationQueryService queryService;

    @GetMapping("/history/{userId}")
    @Operation(
            summary = "Get user application history",
            description = "Users can fetch their own history, Admin can fetch any user's history"
    )
    public ResponseEntity<List<ApplicationHistoryQueryDto>> getHistory(@PathVariable Long userId, @RequestHeader(value = "X-User-Id", required = false) Long headerUserId, @RequestHeader(value = "X-User-Role", required = false) String role) {
        if (headerUserId == null) {
            throw new MissingUserIdHeaderException();
        }

        // User can access only their own history
        if (!headerUserId.equals(userId) && !"ADMIN".equalsIgnoreCase(role)) {
            throw new ForbiddenException("You are not allowed to view this user's application history");
        }
        List<ApplicationHistoryQueryDto> list = queryService.getApplicationsByUser(userId);
        return ResponseEntity.ok(list);
    }
}