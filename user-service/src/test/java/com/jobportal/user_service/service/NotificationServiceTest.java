package com.jobportal.user_service.service;

import com.jobportal.user_service.dto.JobStatusNotificationRequest;
import com.jobportal.user_service.entity.UserCredential;
import com.jobportal.user_service.entity.UserProfile;
import com.jobportal.user_service.enums.ApplicationStatus;
import com.jobportal.user_service.exception.InvalidJobStatusException;
import com.jobportal.user_service.exception.UserNotFound;
import com.jobportal.user_service.repository.UserCredentialRepository;
import com.jobportal.user_service.repository.UserProfileRepository;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class NotificationServiceTest {

    @Test
    void selectedStatus_shouldSendSelectedEmail() {
        UserProfileRepository profileRepo = mock(UserProfileRepository.class);
        UserCredentialRepository credRepo = mock(UserCredentialRepository.class);
        EmailService emailService = mock(EmailService.class);

        NotificationService service =
                new NotificationService(profileRepo, credRepo, emailService);

        UserProfile profile = new UserProfile();
        profile.setId(1L);
        profile.setFirstName("John");
        profile.setLastName("Doe");

        UserCredential cred = new UserCredential();
        cred.setUserId(1L);
        cred.setEmail("john@mail.com");

        when(profileRepo.findById(1L)).thenReturn(Optional.of(profile));
        when(credRepo.findById(1L)).thenReturn(Optional.of(cred));

        JobStatusNotificationRequest req = new JobStatusNotificationRequest();
        req.setUserId(1L);
        req.setJobId(10L);
        req.setStatus(ApplicationStatus.SELECTED);

        String result = service.sendJobStatusNotification(req);

        assertEquals("Notification email sent to john@mail.com", result);
        verify(emailService).sendEmail(
                eq("john@mail.com"),
                eq("Job Application Selected"),
                contains("SELECTED")
        );
    }

    @Test
    void rejectedStatus_shouldSendRejectedEmail() {
        UserProfileRepository profileRepo = mock(UserProfileRepository.class);
        UserCredentialRepository credRepo = mock(UserCredentialRepository.class);
        EmailService emailService = mock(EmailService.class);

        NotificationService service =
                new NotificationService(profileRepo, credRepo, emailService);

        UserProfile profile = new UserProfile();
        profile.setId(2L);
        profile.setFirstName("Jane");
        profile.setLastName("Smith");

        UserCredential cred = new UserCredential();
        cred.setUserId(2L);
        cred.setEmail("jane@mail.com");

        when(profileRepo.findById(2L)).thenReturn(Optional.of(profile));
        when(credRepo.findById(2L)).thenReturn(Optional.of(cred));

        JobStatusNotificationRequest req = new JobStatusNotificationRequest();
        req.setUserId(2L);
        req.setJobId(22L);
        req.setStatus(ApplicationStatus.REJECTED);

        service.sendJobStatusNotification(req);

        verify(emailService).sendEmail(
                eq("jane@mail.com"),
                eq("Job Application Update"),
                contains("REJECTED")
        );
    }

    @Test
    void reviewedStatus_shouldThrowInvalidJobStatusException() {
        UserProfileRepository profileRepo = mock(UserProfileRepository.class);
        UserCredentialRepository credRepo = mock(UserCredentialRepository.class);
        EmailService emailService = mock(EmailService.class);

        NotificationService service = new NotificationService(profileRepo, credRepo, emailService);

        Long userId = 1L;

        UserProfile profile = new UserProfile();
        profile.setId(userId);
        profile.setFirstName("A");
        profile.setLastName("B");

        UserCredential cred = new UserCredential();
        cred.setUserId(userId);
        cred.setEmail("a@b.com");

        when(profileRepo.findById(userId)).thenReturn(Optional.of(profile));
        when(credRepo.findById(userId)).thenReturn(Optional.of(cred));

        JobStatusNotificationRequest req = new JobStatusNotificationRequest();
        req.setUserId(userId);
        req.setJobId(10L);
        req.setStatus(ApplicationStatus.REVIEWED);

        assertThrows(InvalidJobStatusException.class, () -> service.sendJobStatusNotification(req));
        verify(emailService, never()).sendEmail(anyString(), anyString(), anyString());
    }


    @Test
    void profileNotFound_shouldThrowUserNotFound() {
        UserProfileRepository profileRepo = mock(UserProfileRepository.class);

        when(profileRepo.findById(1L)).thenReturn(Optional.empty());

        NotificationService service =
                new NotificationService(
                        profileRepo,
                        mock(UserCredentialRepository.class),
                        mock(EmailService.class)
                );

        JobStatusNotificationRequest req = new JobStatusNotificationRequest();
        req.setUserId(1L);
        req.setStatus(ApplicationStatus.SELECTED);

        assertThrows(UserNotFound.class,
                () -> service.sendJobStatusNotification(req));
    }
}
