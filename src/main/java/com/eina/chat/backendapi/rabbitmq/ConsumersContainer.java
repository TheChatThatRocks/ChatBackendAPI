package com.eina.chat.backendapi.rabbitmq;

import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class ConsumersContainer {
    /**
     * used to store global consumers and queues
     */
    private final Map<String, SimpleMessageListenerContainer> consumers;

    public ConsumersContainer() {
        consumers = new ConcurrentHashMap<>();
    }

    public void addConsumer(String username, SimpleMessageListenerContainer listener) {
        consumers.put(username, listener);
    }

    public void deleteConsumer(String username) {
        consumers.remove(username);
    }

    public SimpleMessageListenerContainer getConsumer(String username) {
        return consumers.get(username);
    }
}