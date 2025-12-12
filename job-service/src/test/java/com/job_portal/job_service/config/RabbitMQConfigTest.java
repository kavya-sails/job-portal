package com.job_portal.job_service.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class RabbitMQConfigTest {

    @Test
    void beans_areCreatedCorrectly() {
        RabbitMQConfig cfg = new RabbitMQConfig();

        TopicExchange exchange = cfg.eventExchange();
        Queue queue = cfg.notificationQueue();
        Binding binding = cfg.binding(queue, exchange);

        assertThat(exchange).isNotNull();
        assertThat(queue).isNotNull();
        assertThat(binding).isNotNull();
        assertThat(binding.getExchange()).isEqualTo(RabbitMQConfig.EXCHANGE);
        assertThat(binding.getRoutingKey()).isEqualTo(RabbitMQConfig.ROUTING_KEY);

        ObjectMapper mapper = new ObjectMapper();
        var converter = cfg.messageConverter(mapper);
        assertThat(converter).isNotNull();

        var connectionFactory = mock(org.springframework.amqp.rabbit.connection.ConnectionFactory.class);
        RabbitTemplate template = cfg.rabbitTemplate(connectionFactory, converter);
        assertThat(template).isNotNull();
        assertThat(template.getMessageConverter()).isEqualTo(converter);
    }
}