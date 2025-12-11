package com.job_portal.job_service.controller.query;

import com.job_portal.job_service.dto.query.ApplicationHistoryQueryDto;
import com.job_portal.job_service.exception.BadRequestException;
import com.job_portal.job_service.exception.ForbiddenException;
import com.job_portal.job_service.service.query.ApplicationQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/jobs/applications")
@RequiredArgsConstructor
public class ApplicationQueryController {
    private final ApplicationQueryService queryService;

    @GetMapping("/history/{userId}")
    public ResponseEntity<List<ApplicationHistoryQueryDto>> getHistory(@PathVariable String userId, @RequestHeader("X-User-Id") String headerUserId, @RequestHeader("X-User-Role") String role) {
        if (!StringUtils.hasText(headerUserId)) {
            throw new BadRequestException("Missing or empty X-User-Id header");
        }

        // User can access only their own history
        if (!headerUserId.equals(userId) && !"ADMIN".equalsIgnoreCase(role)) {
            throw new ForbiddenException("Only ADMIN or USER allowed to query applications");
        }
        List<ApplicationHistoryQueryDto> list = queryService.getApplicationsByUser(userId);
        return ResponseEntity.ok(list);
    }
}