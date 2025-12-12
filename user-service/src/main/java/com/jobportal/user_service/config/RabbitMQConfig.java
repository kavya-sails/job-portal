package com.jobportal.user_service.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {
    public static final String JOB_STATUS_QUEUE = "job_application_status";
    public static final String JOB_EXCHANGE = "job-events-exchange";
    public static final String JOB_ROUTING_KEY = "job.status.updated";

    // Create Queue
    @Bean
    public Queue jobStatusQueue() {
        return new Queue(JOB_STATUS_QUEUE, true); // durable queue
    }

    //  Create Exchange
    @Bean
    public TopicExchange jobExchange() {
        return new TopicExchange(JOB_EXCHANGE);
    }

    // Bind Queue to Exchange using routing key
    @Bean
    public Binding jobStatusBinding() {
        return BindingBuilder
                .bind(jobStatusQueue())
                .to(jobExchange())
                .with(JOB_ROUTING_KEY);
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(
            ConnectionFactory connectionFactory,
            MessageConverter messageConverter
    ) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setMessageConverter(messageConverter);
        return factory;
    }
}