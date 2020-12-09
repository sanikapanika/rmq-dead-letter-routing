package com.sanjo.vaix.entitystream.demo.infrastructure.rabbitmq;

import org.springframework.amqp.core.Queue;

public class RetryQueues {
    private Queue waitQueue;
    private long initialInterval;
    private double factor;
    private long maxWait;
    private int maxRetries;

    public RetryQueues(int maxRetries, long initialInterval, double factor, long maxWait, Queue waitQueue) {
        this.waitQueue = waitQueue;
        this.initialInterval = initialInterval;
        this.factor = factor;
        this.maxRetries = maxRetries;
        this.maxWait = maxWait;
    }

    public boolean isRetriesExhausted(int retry) {
        return retry >= maxRetries;
    }

    public String getQueueName() {
        return waitQueue.getName();
    }

    public long getTimeToWait(int retry) {
        double time = initialInterval * Math.pow(factor, (double) retry);
        if (time > maxWait) {
            return maxWait;
        }

        return (long) time;
    }
}
