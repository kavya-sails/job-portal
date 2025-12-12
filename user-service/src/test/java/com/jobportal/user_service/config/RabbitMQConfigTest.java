package com.jobportal.user_service.config;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.DirectFieldAccessor;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class RabbitMQConfigTest {

    private RabbitMQConfig config;

    @BeforeEach
    void setup() {
        config = new RabbitMQConfig();
    }

    @Test
    void testJobStatusQueue() {
        Queue q = config.jobStatusQueue();

        assertNotNull(q);
        assertEquals(RabbitMQConfig.JOB_STATUS_QUEUE, q.getName());
        assertTrue(q.isDurable());
    }

    @Test
    void testJobExchange() {
        TopicExchange ex = config.jobExchange();

        assertNotNull(ex);
        assertEquals(RabbitMQConfig.JOB_EXCHANGE, ex.getName());
    }

    @Test
    void testJobStatusBinding() {
        // Uses actual beans
        Queue q = config.jobStatusQueue();
        TopicExchange ex = config.jobExchange();

        Binding b = config.jobStatusBinding();

        assertNotNull(b);
        assertEquals(q.getName(), b.getDestination());
        assertEquals(ex.getName(), b.getExchange());
        assertEquals(RabbitMQConfig.JOB_ROUTING_KEY, b.getRoutingKey());
    }

    @Test
    void testJsonMessageConverter() {
        MessageConverter converter = config.jsonMessageConverter();

        assertNotNull(converter);
        assertEquals(
                "org.springframework.amqp.support.converter.Jackson2JsonMessageConverter",
                converter.getClass().getName()
        );
    }



    @Test
    void testRabbitListenerContainerFactory() {
        ConnectionFactory connectionFactory = mock(ConnectionFactory.class);
        MessageConverter converter = config.jsonMessageConverter();

        SimpleRabbitListenerContainerFactory factory =
                config.rabbitListenerContainerFactory(connectionFactory, converter);

        assertNotNull(factory);

        // Read private field "messageConverter"
        DirectFieldAccessor accessor = new DirectFieldAccessor(factory);

        Object mc = accessor.getPropertyValue("messageConverter");
        assertEquals(converter, mc);

        // Read private field "connectionFactory"
        Object cf = accessor.getPropertyValue("connectionFactory");
        assertEquals(connectionFactory, cf);
    }





}
