package com.jobportal.user_service.service;

import com.jobportal.user_service.dto.JobStatusNotificationRequest;
import com.jobportal.user_service.exception.UserNotFound;

import com.jobportal.user_service.repository.UserCredentialRepository;
import com.jobportal.user_service.repository.UserProfileRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NotificationServiceExceptionTest {

    @Mock
    private UserProfileRepository userProfileRepository;

    @Mock
    private UserCredentialRepository userCredentialRepository;

    @Mock
    private EmailService emailService;

    @InjectMocks
    private NotificationService notificationService;

    @Test
    void testSendJobStatusNotification_UserProfileNotFound() {

        //  Arrange
        JobStatusNotificationRequest request = new JobStatusNotificationRequest();
        request.setUserId(999L);
        request.setJobId(1001L);
        request.setStatus("ACCEPTED");

        when(userProfileRepository.findById(999L))
                .thenReturn(Optional.empty());

        //  Act & Assert
        assertThrows(UserNotFound.class,
                () -> notificationService.sendJobStatusNotification(request));

        //  Ensure email not sent
        verify(emailService, never()).sendEmail(any(), any(), any());
    }
}
