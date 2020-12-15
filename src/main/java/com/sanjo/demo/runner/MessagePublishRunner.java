package com.sanjo.demo.runner;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class MessagePublishRunner implements CommandLineRunner {

    private final RabbitTemplate rabbitTemplate;
    private int messageNumber = 0;

    public MessagePublishRunner(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    @Override
    public void run(String... args) {
        System.out.println("Sending message...");

        rabbitTemplate.convertAndSend("entity_stream", "r1", "Message " + messageNumber++);
    }
}
