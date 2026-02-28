package com.jobportal.user_service.messaging;

import com.jobportal.user_service.config.RabbitMQConfig;
import com.jobportal.user_service.dto.JobStatusNotificationRequest;
import com.jobportal.user_service.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.AmqpRejectAndDontRequeueException;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class JobStatusListener {
    private final NotificationService notificationService;

    @RabbitListener(queues = RabbitMQConfig.JOB_STATUS_QUEUE)
    public void receiveJobStatus(JobStatusNotificationRequest message) {
        log.info(" Received Job Status Message: {}", message);
        try{
            notificationService.sendJobStatusNotification(message);
        } catch (Exception e){
            throw new AmqpRejectAndDontRequeueException("Error processing Job Status Message: " + e.getMessage());
        }
    }
}