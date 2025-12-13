package com.jobportal.user_service.service;

import com.jobportal.user_service.dto.JobStatusNotificationRequest;
import com.jobportal.user_service.entity.UserCredential;
import com.jobportal.user_service.entity.UserProfile;
import com.jobportal.user_service.enums.ApplicationStatus;
import com.jobportal.user_service.exception.InvalidJobStatusException;
import com.jobportal.user_service.exception.UserNotFound;
import com.jobportal.user_service.repository.UserProfileRepository;
import com.jobportal.user_service.repository.UserCredentialRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NotificationService {
    private final UserProfileRepository userProfileRepository;
    private final UserCredentialRepository userCredentialRepository;
    private final EmailService emailService;

    public String sendJobStatusNotification(JobStatusNotificationRequest request) {
        //  Fetch profile for name
        UserProfile userProfile = userProfileRepository.findById(request.getUserId())
                .orElseThrow(() -> new UserNotFound("User profile not found with id: " + request.getUserId()));

        // Fetch auth user for email
        UserCredential userCredential = userCredentialRepository.findById(request.getUserId())
                .orElseThrow(() -> new UserNotFound("Auth user not found with id: " + request.getUserId()));

        String email = userCredential.getEmail();
        if (email == null || email.isEmpty()) {
            throw new RuntimeException("User email not available");
        }

        String subject;
        String body;
        String fullName = userProfile.getFirstName() + " " + userProfile.getLastName();

        if (ApplicationStatus.SELECTED.equals(request.getStatus())) {
            subject = "Job Application Selected";
            body = "Hi " + fullName +
                    ",\n\nYou have been SELECTED for Job ID: " +
                    request.getJobId() + ".\n\nCongratulations!";

        } else if (ApplicationStatus.REJECTED.equals(request.getStatus())) {
            subject = "Job Application Update";
            body = "Hi " + fullName +
                    ",\n\nWe’re sorry to inform you that your application for Job ID: " +
                    request.getJobId() + " has been REJECTED.\n\nBetter luck next time!";
        } else {
            throw new InvalidJobStatusException("Invalid status value. Use ACCEPTED or REJECTED");
        }

        // Send email
        emailService.sendEmail(email, subject, body);
        return "Notification email sent to " + email;
    }
}
