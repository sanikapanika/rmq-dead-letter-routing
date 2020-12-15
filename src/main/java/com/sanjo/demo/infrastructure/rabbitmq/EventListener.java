package com.sanjo.demo.infrastructure.rabbitmq;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;


@Component
public class EventListener {

    @Value("${rabbitmq.max-retries}")
    private Integer maxRetries;
    private final RabbitTemplate rabbitTemplate;
    private static final Logger log = LoggerFactory.getLogger(EventListener.class);

    public EventListener(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    @RabbitListener(queues = "event", containerFactory = "listenerContainerFactory")
    public void receiveEvent(Message message) {
        log.info("Received message: {}", message.toString());
        Integer retriesCnt = (Integer) message.getMessageProperties().getHeaders().get("x-retries-count");
        if (retriesCnt == null)
            retriesCnt = 1;
        if (retriesCnt > maxRetries) {
            log.info("Message acked.");
            return;
        }
        log.info("Rejected. Retrying message for the {}. time", retriesCnt);
        message.getMessageProperties().getHeaders().put("x-retries-count", ++retriesCnt);
        rabbitTemplate.send("event.dlx", "r1", message);
    }
}
