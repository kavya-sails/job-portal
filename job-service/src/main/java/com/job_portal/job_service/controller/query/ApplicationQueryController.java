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
@RequestMapping("/api/applications")
@RequiredArgsConstructor
public class ApplicationQueryController {
    private final ApplicationQueryService queryService;

    @GetMapping("/history/{userId}")
    public ResponseEntity<List<ApplicationHistoryQueryDto>> getHistory(@PathVariable String userId) {
        List<ApplicationHistoryQueryDto> list = queryService.getApplicationsByUser(userId);
        return ResponseEntity.ok(list);
    }

    @GetMapping("/dashboard/{userId}")
    public ResponseEntity<List<ApplicationHistoryQueryDto>> getDashboard(@PathVariable String userId) {
        // For now same as history (job title included). Could include aggregated stats in future.
        List<ApplicationHistoryQueryDto> list = queryService.getApplicationsByUser(userId);
        return ResponseEntity.ok(list);
    }
}