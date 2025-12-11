package com.jobportal.user_service.messaging;

import com.jobportal.user_service.config.RabbitMQConfig;
import com.jobportal.user_service.dto.JobStatusNotificationRequest;
import com.jobportal.user_service.service.NotificationService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class JobStatusListener {

    @Autowired
    private NotificationService notificationService;

    //  LISTEN TO THE JOB STATUS QUEUE
    @RabbitListener(queues = RabbitMQConfig.JOB_STATUS_QUEUE)
    public void receiveJobStatus(JobStatusNotificationRequest message) {

        System.out.println(" Received Job Status Message: " + message);

        // Pass the message to your existing notification service
        notificationService.sendJobStatusNotification(message);
    }
}
