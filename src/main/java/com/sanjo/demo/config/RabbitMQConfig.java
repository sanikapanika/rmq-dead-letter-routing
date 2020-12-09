package com.sanjo.demo.config;

import com.sanjo.demo.infrastructure.rabbitmq.EventListener;
import org.aopalliance.aop.Advice;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.adapter.MessageListenerAdapter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import java.util.HashMap;
import java.util.Map;


@Configuration
public class RabbitMQConfig {

    public final Environment env;

    public RabbitMQConfig(Environment env) {
        this.env = env;
    }


    @Bean
    public Queue eventQueue() {
        return new Queue("event");
    }

    @Bean
    public Queue waitQueue() {
        Map<String, Object> args = new HashMap<>();
        args.put("x-dead-letter-exchange", "dead_letter_event");
        args.put("x-dead-letter-routing-key", "r2");
        args.put("x-message-ttl", 10000);
        return new Queue("wait", true, false, false, args);
    }

    @Bean
    TopicExchange entityStreamExchange() {
        return new TopicExchange("entity_stream");
    }

    @Bean
    TopicExchange deadLetterEventExchange() {
        return new TopicExchange("dead_letter_event");
    }

    @Bean
    MessageListenerAdapter eventListenerBean(@Qualifier("eventListener") EventListener eventListener) {
        return new MessageListenerAdapter(eventListener, "receiveEvent");
    }

    @Bean
    Binding DLQToDLXBinding() {
        return BindingBuilder.bind(waitQueue()).to(deadLetterEventExchange()).with("r1");
    }

    @Bean
    Binding EQtoDLXBinding() {
        return BindingBuilder.bind(eventQueue()).to(deadLetterEventExchange()).with("r2");
    }

    @Bean
    ConnectionFactory connectionFactory() {
        CachingConnectionFactory connectionFactory = new CachingConnectionFactory();
        connectionFactory.setUsername("myuser");
        connectionFactory.setPassword("mypass");
        connectionFactory.setHost("127.0.0.1");
        connectionFactory.setPort(5672);

        return connectionFactory;
    }

    @Bean
    RabbitTemplate rabbitTemplate(@Qualifier("connectionFactory") ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate();
        rabbitTemplate.setConnectionFactory(connectionFactory);

        return rabbitTemplate;
    }
}
