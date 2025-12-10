package com.job_portal.job_service.controller.query;

import com.job_portal.job_service.dto.query.ApplicationHistoryQueryDto;
import com.job_portal.job_service.entity.ApplicationEntity;
import com.job_portal.job_service.service.query.ApplicationQueryService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/jobs/applications")
@RequiredArgsConstructor
public class ApplicationQueryController {
    private final ApplicationQueryService queryService;

    @GetMapping("/history/{userId}")
    public ResponseEntity<List<ApplicationHistoryQueryDto>> getHistory(@RequestHeader("X-User-Id") String userId,@RequestHeader("X-User-Role") String role ) {
        List<ApplicationHistoryQueryDto> list = queryService.getApplicationsByUser(userId);
        return ResponseEntity.ok(list);
    }

    @GetMapping("/dashboard/{userId}")
    public ResponseEntity<List<ApplicationHistoryQueryDto>> getDashboard(@RequestHeader("X-User-Id") String userId,@RequestHeader("X-User-Role")  String role ) {
        // For now same as history (job title included). Could include aggregated status in the future.
        List<ApplicationHistoryQueryDto> list = queryService.getApplicationsByUser(userId);
        return ResponseEntity.ok(list);
    }
}