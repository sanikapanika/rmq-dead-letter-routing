package com.sanjo.demo.runner;

import com.sanjo.demo.infrastructure.rabbitmq.EventListener;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
public class MessagePublishRunner implements CommandLineRunner {

    private final RabbitTemplate rabbitTemplate;
    private final EventListener eventListener;

    public MessagePublishRunner(RabbitTemplate rabbitTemplate, EventListener eventListener) {
        this.rabbitTemplate = rabbitTemplate;
        this.eventListener = eventListener;
    }

    @Override
    public void run(String... args) throws Exception {
        System.out.println("Sending message...");
        MessageProperties props = new MessageProperties();
        props.setHeader("x-retried-count", String.valueOf(0));
        props.setHeader("x-original-excahnge", "entity_stream");
        props.setHeader("x-original-routing-key", "r1");

        Message message = new Message("first message".getBytes(), props);
        rabbitTemplate.send("entity_stream","r1", message);
        eventListener.getLatch().await(10000, TimeUnit.MILLISECONDS);
    }
}
