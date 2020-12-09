package com.sanjo.vaix.entitystream.demo.runner;

import com.sanjo.vaix.entitystream.demo.infrastructure.rabbitmq.EventListener;
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
        rabbitTemplate.convertAndSend("entity_stream", "r1", "entity message");
        eventListener.getLatch().await(10000, TimeUnit.MILLISECONDS);
    }
}
