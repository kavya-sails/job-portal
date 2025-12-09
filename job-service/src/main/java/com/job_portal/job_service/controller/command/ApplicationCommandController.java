package com.job_portal.job_service.controller.command;

import com.job_portal.job_service.entity.ApplicationEntity;
import com.job_portal.job_service.service.command.ApplicationCommandService;
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
    @PostMapping
    public ResponseEntity<ApplicationEntity> apply(@RequestHeader("X-USER-ID") String userId,
                                                   @RequestHeader("X-JOB-ID") Long jobId) {
        ApplicationEntity saved = commandService.apply(userId, jobId);
        return ResponseEntity.ok(saved);
    }
}
