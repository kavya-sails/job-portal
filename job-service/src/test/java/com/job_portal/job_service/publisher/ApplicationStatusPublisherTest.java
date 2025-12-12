package com.job_portal.job_service.publisher;

import com.job_portal.job_service.config.RabbitMQConfig;
import com.job_portal.job_service.dto.event.ApplicationStatusEvent;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import static org.mockito.Mockito.*;

class ApplicationStatusPublisherTest {

    @Test
    void publish_sendsEventToExchange() {
        RabbitTemplate rabbitTemplate = mock(RabbitTemplate.class);
        ApplicationStatusPublisher publisher = new ApplicationStatusPublisher(rabbitTemplate);

        ApplicationStatusEvent ev = ApplicationStatusEvent.builder()
                .applicationId(10L)
                .jobId(20L)
                .userId(30L)
                .jobTitle("T")
                .status(null)
                .build();

        publisher.publish(ev);

        verify(rabbitTemplate).convertAndSend(RabbitMQConfig.EXCHANGE, RabbitMQConfig.ROUTING_KEY, ev);
    }
}