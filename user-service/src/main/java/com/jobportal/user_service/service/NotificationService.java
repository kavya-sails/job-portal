package com.jobportal.user_service.service;

import com.jobportal.user_service.dto.JobStatusNotificationRequest;
import com.jobportal.user_service.entity.UserCredential;
import com.jobportal.user_service.entity.UserProfile;
import com.jobportal.user_service.exception.InvalidJobStatusException;
import com.jobportal.user_service.exception.UserNotFound;

import com.jobportal.user_service.repository.UserProfileRepository;
import com.jobportal.user_service.repository.UserCredentialRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class NotificationService {

    @Autowired
    private UserProfileRepository userProfileRepository;

    @Autowired
    private UserCredentialRepository userCredentialRepository;

    @Autowired
    private EmailService emailService;

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

        //  Build message
        String subject;
        String body;
        String fullName = userProfile.getFirstName() + " " + userProfile.getLastName();

        if ("ACCEPTED".equalsIgnoreCase(request.getStatus())) {

            subject = "Job Application Selected";
            body = "Hi " + fullName +
                    ",\n\nYou have been SELECTED for Job ID: " +
                    request.getJobId() + ".\n\nCongratulations!";

        } else if ("REJECTED".equalsIgnoreCase(request.getStatus())) {

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
