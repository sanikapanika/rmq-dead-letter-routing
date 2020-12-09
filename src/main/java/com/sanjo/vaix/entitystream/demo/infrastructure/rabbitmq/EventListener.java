package com.sanjo.vaix.entitystream.demo.infrastructure.rabbitmq;

import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.util.concurrent.CountDownLatch;

@Component
public class EventListener {

    private CountDownLatch latch = new CountDownLatch(1);

    @RabbitListener(containerFactory = "retryQueueContainerFactory", ackMode = "MANUAL", bindings = @QueueBinding(value = @Queue(value = "event", durable = "true"),
            exchange = @Exchange(value = "entity_stream",
                    ignoreDeclarationExceptions = "true",
                    type = "topic"),
            key = "r1"))
    public void receiveEvent(String message) throws Exception {
        System.out.println("Received: <" + message + ">");
        latch.countDown();
        throw new Exception("Exception");
    }


    public CountDownLatch getLatch() {
        return latch;
    }
}
