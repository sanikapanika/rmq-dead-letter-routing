package com.sanjo.demo.infrastructure.rabbitmq;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class EventListenerConfig {

    private final RabbitAdmin rabbitAdmin;

    public EventListenerConfig(RabbitAdmin rabbitAdmin) {
        this.rabbitAdmin = rabbitAdmin;
    }

    @Bean
    public Queue eventQueue() {
        return QueueBuilder.durable("event")
                .withArgument("x-dead-letter-exchange", "event.dlx")
                .withArgument("x-dead-letter-routing-key", "r1")
                .build();
    }

    @Bean
    public TopicExchange entityStreamExchange() {
        return new TopicExchange("entity_stream");
    }

    @Bean
    public Queue waitQueue() {
        return QueueBuilder.durable("wait")
                .withArgument("x-dead-letter-exchange", "event.dlx")
                .withArgument("x-dead-letter-routing-key", "r2")
                .withArgument("x-message-ttl", 10000)
                .build();
    }

    @Bean
    public Binding DLQToDLXBinding() {
        return BindingBuilder.bind(waitQueue()).to(deadLetterExchange()).with("r1");
    }

    @Bean
    public Binding EQtoDLXBinding() {
        return BindingBuilder.bind(eventQueue()).to(deadLetterExchange()).with("r2");
    }

    @Bean
    public TopicExchange deadLetterExchange() {
        return new TopicExchange("event.dlx");
    }
}
