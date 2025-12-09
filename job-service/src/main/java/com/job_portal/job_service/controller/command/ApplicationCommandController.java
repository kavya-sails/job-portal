package com.job_portal.job_service.controller.command;

import com.job_portal.job_service.dto.command.ApplyJobCommand;
import com.job_portal.job_service.entity.ApplicationEntity;
import com.job_portal.job_service.service.command.ApplicationCommandService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/applications")
@RequiredArgsConstructor
public class ApplicationCommandController
{
    private final ApplicationCommandService commandService;

    @PostMapping
    public ResponseEntity<ApplicationEntity> apply(@Valid @RequestBody ApplyJobCommand command) {
        ApplicationEntity saved = commandService.apply(command);
        return ResponseEntity.ok(saved);
    }
}
