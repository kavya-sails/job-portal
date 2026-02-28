package com.jobportal.user_service.messaging;

import com.jobportal.user_service.dto.JobStatusNotificationRequest;
import com.jobportal.user_service.enums.ApplicationStatus;
import com.jobportal.user_service.service.NotificationService;
import org.junit.jupiter.api.Test;

import org.springframework.amqp.AmqpRejectAndDontRequeueException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class JobStatusListenerTest {

    private final NotificationService notificationService = mock(NotificationService.class);
    private final JobStatusListener listener = new JobStatusListener(notificationService);

    // SUCCESS PATH
    @Test
    void testReceiveJobStatus_success() {
        JobStatusNotificationRequest req =
                new JobStatusNotificationRequest(1L, 10L, ApplicationStatus.SELECTED);

        listener.receiveJobStatus(req);

        verify(notificationService, times(1))
                .sendJobStatusNotification(req);
    }

    //  FAILURE PATH
    @Test
    void testReceiveJobStatus_failure() {
        JobStatusNotificationRequest req =
                new JobStatusNotificationRequest(1L, 20L, ApplicationStatus.REJECTED);

        doThrow(new RuntimeException("Test Failure"))
                .when(notificationService).sendJobStatusNotification(req);

        AmqpRejectAndDontRequeueException ex =
                assertThrows(AmqpRejectAndDontRequeueException.class, () -> listener.receiveJobStatus(req));

        assertTrue(ex.getMessage().contains("Error processing Job Status Message"));
    }
}
