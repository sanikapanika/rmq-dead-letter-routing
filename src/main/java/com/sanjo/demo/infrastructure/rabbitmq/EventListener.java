package com.sanjo.demo.infrastructure.rabbitmq;

import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.CountDownLatch;

@Component
public class EventListener {

    private CountDownLatch latch = new CountDownLatch(1);
    private final RabbitTemplate rabbitTemplate;

    public EventListener(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    @RabbitListener(containerFactory = "rabbitListenerContainerFactory", ackMode = "MANUAL", bindings = @QueueBinding(value = @Queue(value = "event", durable = "true"),
            exchange = @Exchange(value = "entity_stream",
                    ignoreDeclarationExceptions = "true",
                    type = "topic"),
            key = "r1"))
    public void receiveEvent(Message message, Channel channel) throws Exception {
        System.out.println("Received: <" + new String(message.getBody()) + ">");
        latch.countDown();

        if (Integer.parseInt(message.getMessageProperties().getHeader("x-retried-count")) >= 6) {
            channel.basicReject(message.getMessageProperties().getDeliveryTag(), false);
        } else {
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
            int retries = Integer.parseInt(message.getMessageProperties().getHeader("x-retried-count"));
            retries++;
            message.getMessageProperties().setHeader("x-retried-count", String.valueOf(retries));
            rabbitTemplate.send("dead_letter_event", "r1", message);
        }
    }


    public CountDownLatch getLatch() {
        return latch;
    }
}
