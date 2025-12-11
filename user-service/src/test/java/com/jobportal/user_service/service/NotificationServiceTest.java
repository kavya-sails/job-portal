package com.jobportal.user_service.service;

import com.jobportal.user_service.dto.JobStatusNotificationRequest;
import com.jobportal.user_service.entity.UserCredential;
import com.jobportal.user_service.entity.UserProfile;
import com.jobportal.user_service.repository.AuthUserRepository;
import com.jobportal.user_service.repository.UserProfileRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NotificationServiceTest {

    @Mock
    private UserProfileRepository userProfileRepository;

    @Mock
    private AuthUserRepository authUserRepository;

    @Mock
    private EmailService emailService;

    @InjectMocks
    private NotificationService notificationService;

    @Test
    void testSendJobStatusNotification_Accepted() {

        //  Arrange
        JobStatusNotificationRequest request = new JobStatusNotificationRequest();
        request.setUserId(1L);
        request.setJobId(1001L);
        request.setStatus("ACCEPTED");

        UserProfile profile = new UserProfile();
        profile.setId(1L);
        profile.setFirstName("Test");
        profile.setLastName("User");

        UserCredential userCredential = new UserCredential();
        userCredential.setUserId(1L);
        userCredential.setEmail("test@gmail.com");

        when(userProfileRepository.findById(1L))
                .thenReturn(Optional.of(profile));

        when(authUserRepository.findById(1L))
                .thenReturn(Optional.of(userCredential));

        //  Act
        String response = notificationService.sendJobStatusNotification(request);

        //  Assert
        assertTrue(response.contains("Notification email sent"));

        verify(emailService, times(1))
                .sendEmail(eq("test@gmail.com"), anyString(), anyString());
    }
}
