package com.jobportal.user_service.controller;

import com.jobportal.user_service.dto.JobStatusNotificationRequest;
import com.jobportal.user_service.service.NotificationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {
    private final NotificationService notificationService;

    // called by Job Service
    @PostMapping("/job-status")
    public String sendJobStatusNotification(@Valid @RequestBody JobStatusNotificationRequest request) {
        return notificationService.sendJobStatusNotification(request);
    }
}