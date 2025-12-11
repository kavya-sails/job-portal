package com.jobportal.user_service.controller;

import com.jobportal.user_service.dto.JobStatusNotificationRequest;
import com.jobportal.user_service.service.NotificationService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    @Autowired
    private NotificationService notificationService;

    // called by Job Service
    @PostMapping("/job-status")
    public String sendJobStatusNotification(@Valid @RequestBody JobStatusNotificationRequest request) {
        return notificationService.sendJobStatusNotification(request);
    }
}
