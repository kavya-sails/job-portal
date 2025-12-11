package com.job_portal.job_service.controller.query;
import com.job_portal.job_service.dto.query.ApplicationHistoryQueryDto;
import com.job_portal.job_service.service.query.ApplicationQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/jobs/applications")
@RequiredArgsConstructor
public class ApplicationQueryController {
    private final ApplicationQueryService queryService;

    @GetMapping("/history/{userId}")
    public ResponseEntity<List<ApplicationHistoryQueryDto>> getHistory( @PathVariable String userId,                 // userId from URL
                                                                        @RequestHeader("X-User-Id") String headerUserId, @RequestHeader("X-User-Role") String role) {
        if (headerUserId == null) {
            return ResponseEntity.badRequest().build();
        }

        // User can access only their own history
        if (!headerUserId.equals(userId)) {

            // Admin can access any user's history
            if (!"ADMIN".equalsIgnoreCase(role)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
        }
        List<ApplicationHistoryQueryDto> list = queryService.getApplicationsByUser(userId);
        return ResponseEntity.ok(list);
    }
}